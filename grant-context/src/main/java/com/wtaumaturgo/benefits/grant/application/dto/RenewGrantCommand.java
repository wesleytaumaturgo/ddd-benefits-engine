package com.wtaumaturgo.benefits.grant.application.dto;

import com.wtaumaturgo.benefits.grant.domain.model.GrantId;
import com.wtaumaturgo.benefits.shared.domain.model.Period;

public record RenewGrantCommand(GrantId grantId, Period newValidity) {
}
