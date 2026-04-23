package com.wtaumaturgo.benefits.shared.support;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Singleton Testcontainers base. Extend this class in any integration test
 * that requires a real PostgreSQL 16 instance.
 *
 * <p>{@code withReuse(true)} requires {@code testcontainers.reuse.enable=true}
 * in {@code ~/.testcontainers.properties} or the environment variable
 * {@code TESTCONTAINERS_REUSE_ENABLE=true} (used in CI). Absence of the flag
 * is acceptable — tests still run, just slower.</p>
 */
public abstract class PostgreSQLContainerBase {

    static final PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"))
            .withDatabaseName("benefits_test")
            .withUsername("benefits")
            .withPassword("benefits")
            .withReuse(true);
        POSTGRES.start();
    }

    protected static String jdbcUrl() {
        return POSTGRES.getJdbcUrl() + "&stringtype=unspecified";
    }

    protected static String username() {
        return POSTGRES.getUsername();
    }

    protected static String password() {
        return POSTGRES.getPassword();
    }
}
