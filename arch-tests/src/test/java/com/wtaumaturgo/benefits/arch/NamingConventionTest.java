package com.wtaumaturgo.benefits.arch;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Validates naming conventions enforced across all bounded contexts.
 *
 * <p>Naming is part of the Ubiquitous Language — deviations are architecture
 * violations, not stylistic preferences. These rules ensure that anyone reading
 * the codebase can map a class name to its layer without guessing.
 */
@AnalyzeClasses(
    packages = "com.wtaumaturgo.benefits",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class NamingConventionTest {

    @ArchTest
    static final ArchRule use_case_classes_must_reside_in_application =
        classes()
            .that().haveSimpleNameEndingWith("UseCase")
            .should().resideInAPackage("..application..")
            .because("Classes with the UseCase suffix must live in the application layer")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule controllers_must_reside_in_api =
        classes()
            .that().haveSimpleNameEndingWith("Controller")
            .and().areNotInterfaces()
            .should().resideInAPackage("..api..")
            .because("Controllers must reside in the api package")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule jpa_entities_must_be_suffixed_with_JpaEntity =
        classes()
            .that().resideInAPackage("..infrastructure.persistence..")
            .and().areAnnotatedWith("jakarta.persistence.Entity")
            .should().haveSimpleNameEndingWith("JpaEntity")
            .because("JPA entities must be named *JpaEntity to distinguish from domain aggregates")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule repository_implementations_must_live_in_infrastructure =
        classes()
            .that().haveSimpleNameEndingWith("RepositoryImpl")
            .should().resideInAPackage("..infrastructure..")
            .because("Repository implementations belong in the infrastructure layer")
            .allowEmptyShould(true);
}
