package com.wtaumaturgo.benefits.grant.arch;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Module-local ArchUnit guard for the Grant bounded context (GRANT-12).
 *
 * <p>Duplicates in MINIATURE what {@code LayeredArchitectureTest} and
 * {@code CrossContextIsolationTest} check globally in the {@code arch-tests}
 * module — the duplication is deliberate: a violation introduced in the grant
 * module must fail {@code ./mvnw -q -pl grant-context test} without requiring
 * the cross-module arch-tests run. Per {@code CLAUDE.md} rule #1, the domain
 * layer must be pure Java; per rule #3, contexts communicate via domain events
 * only — never via direct imports.</p>
 */
@AnalyzeClasses(
    packages = "com.wtaumaturgo.benefits.grant",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class GrantDomainIsolationTest {

    @ArchTest
    static final ArchRule domain_must_not_depend_on_infrastructure =
        noClasses()
            .that().resideInAPackage("..grant.domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("..infrastructure..")
            .because("CLAUDE.md rule #1 — grant.domain must stay pure Java, no JPA/persistence imports");

    @ArchTest
    static final ArchRule grant_domain_must_not_depend_on_wallet =
        noClasses()
            .that().resideInAPackage("..grant.domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("..wallet..")
            .because("CLAUDE.md rule #3 — cross-context communication via domain events, never via direct import")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule grant_domain_must_not_depend_on_redemption =
        noClasses()
            .that().resideInAPackage("..grant.domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("..redemption..")
            .because("CLAUDE.md rule #3 — cross-context communication via domain events, never via direct import")
            .allowEmptyShould(true);
}
