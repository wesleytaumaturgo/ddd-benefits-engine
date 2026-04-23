package com.wtaumaturgo.benefits.grant.infrastructure.persistence;

import com.wtaumaturgo.benefits.grant.domain.model.MerchantCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * JPA entity backing the Grant aggregate — kept distinct from the domain
 * class per CLAUDE.md non-negotiable rule #2. Fields map 1:1 to columns
 * declared in V001__create_grant_tables.sql; Hibernate validates the
 * mapping on startup (ddl-auto=validate).
 */
@Entity
@Table(name = "grant_grants")
class GrantJpaEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "beneficiary_id", columnDefinition = "uuid", nullable = false)
    private UUID beneficiaryId;

    @Column(name = "plan_id", columnDefinition = "uuid", nullable = false)
    private UUID planId;

    @Column(name = "plan_name", nullable = false, length = 255)
    private String planName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "merchant_categories", columnDefinition = "jsonb", nullable = false)
    private Set<MerchantCategory> merchantCategories;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @Column(name = "cycle_year", nullable = false)
    private short cycleYear;

    @Column(name = "cycle_month", nullable = false)
    private short cycleMonth;

    @Column(name = "amount_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal amountValue;

    @Column(name = "amount_currency", nullable = false, length = 3)
    private String amountCurrency;

    @Column(name = "created_at", columnDefinition = "timestamptz", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamptz", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    // Getters and setters — package-private, used only by the mapper in the same package.
    UUID getId() { return id; }
    void setId(UUID id) { this.id = id; }

    UUID getBeneficiaryId() { return beneficiaryId; }
    void setBeneficiaryId(UUID beneficiaryId) { this.beneficiaryId = beneficiaryId; }

    UUID getPlanId() { return planId; }
    void setPlanId(UUID planId) { this.planId = planId; }

    String getPlanName() { return planName; }
    void setPlanName(String planName) { this.planName = planName; }

    Set<MerchantCategory> getMerchantCategories() { return merchantCategories; }
    void setMerchantCategories(Set<MerchantCategory> merchantCategories) { this.merchantCategories = merchantCategories; }

    String getStatus() { return status; }
    void setStatus(String status) { this.status = status; }

    LocalDate getValidFrom() { return validFrom; }
    void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    LocalDate getValidUntil() { return validUntil; }
    void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    short getCycleYear() { return cycleYear; }
    void setCycleYear(short cycleYear) { this.cycleYear = cycleYear; }

    short getCycleMonth() { return cycleMonth; }
    void setCycleMonth(short cycleMonth) { this.cycleMonth = cycleMonth; }

    BigDecimal getAmountValue() { return amountValue; }
    void setAmountValue(BigDecimal amountValue) { this.amountValue = amountValue; }

    String getAmountCurrency() { return amountCurrency; }
    void setAmountCurrency(String amountCurrency) { this.amountCurrency = amountCurrency; }
}
