package com.wtaumaturgo.benefits.grant.application.dto;

import com.wtaumaturgo.benefits.grant.domain.model.GrantId;

public record RevokeGrantCommand(GrantId grantId) {
}
