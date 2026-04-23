package com.wtaumaturgo.benefits.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wtaumaturgo.benefits.shared.support.PostgreSQLContainerBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end integration test for the Grant REST API (GRANT-10 + D-05/D-06).
 * Uses a shared reusable PostgreSQL 16 container via {@link PostgreSQLContainerBase}.
 *
 * <p>Each test uses a fresh random (beneficiaryId, planId) pair so there is no
 * need to truncate tables between tests; the partial unique index enforces the
 * duplicate-active invariant at DB level.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class GrantControllerIT extends PostgreSQLContainerBase {

    @DynamicPropertySource
    static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PostgreSQLContainerBase::jdbcUrl);
        registry.add("spring.datasource.username", PostgreSQLContainerBase::username);
        registry.add("spring.datasource.password", PostgreSQLContainerBase::password);
    }

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private Map<String, Object> newRequest(UUID beneficiaryId, UUID planId) {
        return Map.of(
            "beneficiaryId", beneficiaryId.toString(),
            "planId", planId.toString(),
            "planName", "Plano Gold",
            "categories", Set.of("FUEL_STATION", "RESTAURANT"),
            "amountValue", "150.00",
            "amountCurrency", "BRL",
            "validFrom", "2026-04-01",
            "validUntil", "2026-05-01",
            "cycleYear", 2026,
            "cycleMonth", 4
        );
    }

    @Test
    void postShouldCreateGrantAndReturn201() throws Exception {
        Map<String, Object> req = newRequest(UUID.randomUUID(), UUID.randomUUID());

        mvc.perform(post("/grants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(req)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.grantId").exists())
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.planName").value("Plano Gold"));
    }

    @Test
    void duplicateActivePostShouldReturn422ProblemDetail() throws Exception {
        UUID beneficiary = UUID.randomUUID();
        UUID plan = UUID.randomUUID();
        Map<String, Object> req = newRequest(beneficiary, plan);
        byte[] body = json.writeValueAsBytes(req);

        mvc.perform(post("/grants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());

        mvc.perform(post("/grants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.errorCode").value("GRANT_DUPLICATE_ACTIVE"))
            .andExpect(jsonPath("$.title").value("Concessão duplicada"))
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getShouldReturn200WhenFound() throws Exception {
        UUID beneficiary = UUID.randomUUID();
        UUID plan = UUID.randomUUID();
        Map<String, Object> req = newRequest(beneficiary, plan);

        MvcResult created = mvc.perform(post("/grants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(req)))
            .andExpect(status().isCreated())
            .andReturn();

        JsonNode body = json.readTree(created.getResponse().getContentAsString());
        String grantId = body.get("grantId").asText();

        mvc.perform(get("/grants/" + grantId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.grantId").value(grantId))
            .andExpect(jsonPath("$.beneficiaryId").value(beneficiary.toString()));
    }

    @Test
    void getShouldReturn404WhenNotFound() throws Exception {
        mvc.perform(get("/grants/" + UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturn204() throws Exception {
        UUID beneficiary = UUID.randomUUID();
        UUID plan = UUID.randomUUID();
        Map<String, Object> req = newRequest(beneficiary, plan);

        MvcResult created = mvc.perform(post("/grants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(req)))
            .andExpect(status().isCreated())
            .andReturn();

        String grantId = json.readTree(created.getResponse().getContentAsString()).get("grantId").asText();

        mvc.perform(delete("/grants/" + grantId))
            .andExpect(status().isNoContent());
    }

    @Test
    void renewShouldReturn200() throws Exception {
        UUID beneficiary = UUID.randomUUID();
        UUID plan = UUID.randomUUID();
        Map<String, Object> req = newRequest(beneficiary, plan);

        MvcResult created = mvc.perform(post("/grants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(req)))
            .andExpect(status().isCreated())
            .andReturn();

        String grantId = json.readTree(created.getResponse().getContentAsString()).get("grantId").asText();

        Map<String, Object> renewBody = Map.of(
            "validFrom", "2026-05-01",
            "validUntil", "2026-06-01"
        );

        mvc.perform(put("/grants/" + grantId + "/renew")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(renewBody)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.validFrom").value("2026-05-01"))
            .andExpect(jsonPath("$.validUntil").value("2026-06-01"));

        // Sanity check — the renewed window persists on subsequent GET
        mvc.perform(get("/grants/" + grantId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.validFrom").value("2026-05-01"));

        // Reference variable to silence unused-local compiler warnings on `beneficiary`/`plan`.
        assertThat(beneficiary).isNotEqualTo(plan);
    }
}
