-- V001__create_grant_tables.sql
-- Creates grant_grants — the persistence root of the Grant aggregate.
-- Requirements: GRANT-08 (table separate from domain class), GRANT-09 (prefix grant_).
-- Hibernate runs in ddl-auto=validate; column names and types below are the
-- canonical schema source of truth.

CREATE TABLE grant_grants (
    id                  UUID          PRIMARY KEY,                 -- domain-assigned UUID v7 (no sequence)
    beneficiary_id      UUID          NOT NULL,
    plan_id             UUID          NOT NULL,
    plan_name           VARCHAR(255)  NOT NULL,
    merchant_categories JSONB         NOT NULL,                    -- Set<MerchantCategory> via @JdbcTypeCode(SqlTypes.JSON)
    status              VARCHAR(32)   NOT NULL,                    -- enum name: ACTIVE | REVOKED | EXPIRED
    valid_from          DATE          NOT NULL,
    valid_until         DATE          NOT NULL,
    cycle_year          SMALLINT      NOT NULL,
    cycle_month         SMALLINT      NOT NULL CHECK (cycle_month BETWEEN 1 AND 12),
    amount_value        NUMERIC(19,2) NOT NULL,
    amount_currency     CHAR(3)       NOT NULL,
    created_at          TIMESTAMPTZ   NOT NULL,
    updated_at          TIMESTAMPTZ   NOT NULL,

    CONSTRAINT chk_grant_validity_range CHECK (valid_from < valid_until)
);

-- Query index for existsActiveByBeneficiaryAndPlan + general lookups by beneficiary/plan.
CREATE INDEX idx_grant_beneficiary_plan
    ON grant_grants (beneficiary_id, plan_id, status);

-- Partial unique index — DB-level defense-in-depth for GRANT-02.
-- Guarantees at most one ACTIVE grant per (beneficiary_id, plan_id) even under
-- concurrent inserts that both pass the application-level existsActive check.
CREATE UNIQUE INDEX uq_grant_active_per_beneficiary_plan
    ON grant_grants (beneficiary_id, plan_id)
    WHERE status = 'ACTIVE';

-- Sequence policy (GRANT-09): grant_grants.id is a domain-assigned UUID v7, so no
-- sequence is created for it. When auxiliary tables (audit/history) with
-- Hibernate @GeneratedValue(SEQUENCE) are added in future phases, the sequence
-- MUST be declared as:
--     CREATE SEQUENCE grant_<purpose>_seq INCREMENT BY 50 START WITH 1;
-- INCREMENT BY 50 aligns with @SequenceGenerator(allocationSize=50); any other
-- stride causes PK collisions with Hibernate's pooled optimizer (see Pitfall 2 in
-- 02-RESEARCH.md).
