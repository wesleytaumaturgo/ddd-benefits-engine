package com.wtaumaturgo.benefits.arch;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Validates layered architecture rules across all bounded contexts.
 *
 * <p>Failures here in Phases 2-4 mean a class in {@code domain/} has imported from
 * {@code infrastructure/} or Spring / JPA — a direct violation of the non-negotiable
 * rules listed in {@code CLAUDE.md}. The domain layer must stay pure Java.
 */
@AnalyzeClasses(
    packages = "com.wtaumaturgo.benefits",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class LayeredArchitectureTest {

    @ArchTest
    static final ArchRule domain_must_not_depend_on_infrastructure =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("..infrastructure..")
            .because("Domain layer must be pure — no infrastructure dependencies");

    @ArchTest
    static final ArchRule domain_must_not_use_spring_annotations =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("org.springframework..")
            .because("Domain layer must not import any Spring class");

    @ArchTest
    static final ArchRule domain_must_not_use_jpa_annotations =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("jakarta.persistence..")
            .because("Domain layer must not import any JPA annotation or class");

    @ArchTest
    static final ArchRule application_must_not_depend_on_api =
        noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat()
            .resideInAPackage("..api..")
            .because("Application layer must not depend on the API layer")
            .allowEmptyShould(true);
}
