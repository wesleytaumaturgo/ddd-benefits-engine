package com.wtaumaturgo.benefits.grant.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data JPA repository for GrantJpaEntity. The derived query name
 * {@code existsByBeneficiaryIdAndPlanIdAndStatus} compiles to:
 *   SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
 *     FROM grant_grants
 *    WHERE beneficiary_id = ?1 AND plan_id = ?2 AND status = ?3
 * The composite index {@code idx_grant_beneficiary_plan} supports this path.
 */
interface GrantJpaRepository extends JpaRepository<GrantJpaEntity, UUID> {

    boolean existsByBeneficiaryIdAndPlanIdAndStatus(UUID beneficiaryId, UUID planId, String status);
}
