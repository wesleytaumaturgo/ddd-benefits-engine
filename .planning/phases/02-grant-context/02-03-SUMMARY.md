---
phase: "02"
plan: "03"
subsystem: grant-context
tags:
  - grant-context
  - ddd
  - application
  - use-cases
  - events
dependency_graph:
  requires:
    - 02-02  # Grant aggregate, GrantRepository interface, domain exceptions
    - 01-01  # shared-kernel (Money, Period, Cycle, DomainEvent, AggregateRoot)
  provides:
    - CreateGrantUseCase (GRANT-02 + GRANT-07 contract)
    - RevokeGrantUseCase
    - RenewGrantUseCase
    - GetGrantUseCase
    - CreateGrantCommand / RevokeGrantCommand / RenewGrantCommand
  affects:
    - 02-04  # JPA infrastructure (implements GrantRepository)
    - 02-05  # GrantController (consumes use cases)
    - 02-06  # Integration tests (test use cases end-to-end)
tech_stack:
  added:
    - spring-boot-starter-web (compile)
    - spring-boot-starter-data-jpa (compile)
    - testcontainers:postgresql (test)
    - testcontainers:junit-jupiter (test)
    - postgresql JDBC driver (test)
    - shared-kernel test-jar classifier (test)
  patterns:
    - Application layer as transaction boundary (GRANT-07)
    - Command record DTOs (CreateGrantCommand, RevokeGrantCommand, RenewGrantCommand)
    - save() then pullDomainEvents() then publishEvent() — canonical event publication order
    - Constructor injection for @Service use cases
    - TDD: RED (compile failures) then GREEN (10/10 pass)
key_files:
  created:
    - grant-context/src/main/java/com/wtaumaturgo/benefits/grant/application/dto/CreateGrantCommand.java
    - grant-context/src/main/java/com/wtaumaturgo/benefits/grant/application/dto/RevokeGrantCommand.java
    - grant-context/src/main/java/com/wtaumaturgo/benefits/grant/application/dto/RenewGrantCommand.java
    - grant-context/src/main/java/com/wtaumaturgo/benefits/grant/application/CreateGrantUseCase.java
    - grant-context/src/main/java/com/wtaumaturgo/benefits/grant/application/RevokeGrantUseCase.java
    - grant-context/src/main/java/com/wtaumaturgo/benefits/grant/application/RenewGrantUseCase.java
    - grant-context/src/main/java/com/wtaumaturgo/benefits/grant/application/GetGrantUseCase.java
    - grant-context/src/test/java/com/wtaumaturgo/benefits/grant/application/CreateGrantUseCaseTest.java
    - grant-context/src/test/java/com/wtaumaturgo/benefits/grant/application/RevokeGrantUseCaseTest.java
    - grant-context/src/test/java/com/wtaumaturgo/benefits/grant/application/RenewGrantUseCaseTest.java
    - grant-context/src/test/java/com/wtaumaturgo/benefits/grant/application/GetGrantUseCaseTest.java
  modified:
    - grant-context/pom.xml
decisions:
  - "GetGrantUseCase has no @Transactional — read-only query, Spring default propagation adds overhead without benefit"
  - "publishEvent() called inside @Transactional scope — downstream @TransactionalEventListener(AFTER_COMMIT) consumers will fire post-commit; synchronous listeners fire inline"
  - "Tests use publishEvent(any(Object.class)) to disambiguate overloaded ApplicationEventPublisher method (compile-time resolution issue with raw any())"
metrics:
  duration: "284s"
  completed_date: "2026-04-23"
  tasks_completed: 2
  files_changed: 12
---

# Phase 02 Plan 03: Application Layer Use Cases Summary

Application layer for grant-context implemented with four use cases and three command DTOs, wiring the Spring transaction boundary and domain event publication pipeline (GRANT-02, GRANT-07).

## Updated grant-context/pom.xml

**Before:** only `shared-kernel` compile + `spring-boot-starter-test` test scope.

**After:** added compile-scope `spring-boot-starter-web` and `spring-boot-starter-data-jpa`; test-scope `spring-boot-starter-test`, `shared-kernel` test-jar (classifier `tests`), `testcontainers:postgresql`, `testcontainers:junit-jupiter`, and `postgresql` JDBC driver. No version overrides — all managed by `spring-boot-starter-parent` BOM and `testcontainers-bom`.

## Command DTOs

Three Java records in `grant.application.dto`:

| Record | Fields |
|--------|--------|
| `CreateGrantCommand` | `UUID beneficiaryId`, `UUID planId`, `String planName`, `Set<MerchantCategory> categories`, `Money amount`, `Period validity`, `Cycle cycle` |
| `RevokeGrantCommand` | `GrantId grantId` |
| `RenewGrantCommand` | `GrantId grantId`, `Period newValidity` |

Records are immutable by construction — no validation needed beyond what Grant enforces.

## Use Case Classes

### CreateGrantUseCase

`@Service` — `@Transactional` on `execute(CreateGrantCommand cmd) -> GrantId`.

Constructor-injects `GrantRepository` + `ApplicationEventPublisher`.

Execution sequence:
1. `existsActiveByBeneficiaryAndPlan(beneficiaryId, planId)` — throws `DuplicateActiveGrantException` if true (GRANT-02)
2. `Grant.create(beneficiaryId, BenefitPlan, amount, validity, cycle)` — factory call, GrantCreated event queued internally
3. `repository.save(grant)` — persists the aggregate
4. `grant.pullDomainEvents()` — drains event buffer (copy-before-clear)
5. `events.publishEvent(event)` for each drained event (GRANT-07)
6. Returns `grant.id()`

### RevokeGrantUseCase

`@Service @Transactional` — `execute(RevokeGrantCommand cmd) -> void`.

Sequence: load via `findById` (throws `GrantNotFoundException` if absent) → `grant.revoke()` → `save` → drain → publish.

### RenewGrantUseCase

`@Service @Transactional` — `execute(RenewGrantCommand cmd) -> void`.

Sequence: load (throws `GrantNotFoundException`) → `grant.renew(cmd.newValidity())` → `save` → drain → publish.

### GetGrantUseCase

`@Service` — no `@Transactional` (read-only query, adds overhead without benefit).

Constructor-injects only `GrantRepository`.

Returns `repository.findById(id)` directly as `Optional<Grant>`.

## Verification Results

```
./mvnw -q -pl grant-context compile       → BUILD SUCCESS
./mvnw -q -pl grant-context test          → 10 tests, 0 failures
./mvnw -q -pl arch-tests test             → 14 tests, 0 failures
```

## TDD Gate Compliance

- RED gate commit: `3e0a90d` — failing tests (compile errors, classes did not exist)
- GREEN gate commit: `88a76e4` — all 10 tests pass
- REFACTOR: not required (no structural cleanup needed)

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 1 - Bug] Fixed Mockito ambiguous method reference on ApplicationEventPublisher.publishEvent()**

- **Found during:** Task 2, GREEN phase test execution
- **Issue:** `ApplicationEventPublisher` declares two overloads: `publishEvent(ApplicationEvent)` and `publishEvent(Object)`. Mockito's `verify(events).publishEvent(any())` resolved to `publishEvent(ApplicationEvent)` at compile time, but the use case calls `publishEvent(Object event)`. The verification always reported "Wanted but not invoked" even though the call occurred.
- **Fix:** Changed all `publishEvent(any())` matchers in tests to `publishEvent(any(Object.class))` to explicitly target the `Object` overload.
- **Files modified:** `CreateGrantUseCaseTest.java`, `RevokeGrantUseCaseTest.java`, `RenewGrantUseCaseTest.java`
- **Commit:** `88a76e4`

## Known Stubs

None — all use case methods are fully implemented and delegate to real `GrantRepository` and `ApplicationEventPublisher` contracts. No hardcoded returns or placeholder logic.

## Threat Flags

No new trust boundaries introduced. All surface (ApplicationEventPublisher dispatch, GrantRepository calls) was already modeled in the plan's threat register (T-02-09 through T-02-12).

## Self-Check: PASSED
