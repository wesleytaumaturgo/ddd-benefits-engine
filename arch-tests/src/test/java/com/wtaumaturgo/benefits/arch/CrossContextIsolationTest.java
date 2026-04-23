package com.wtaumaturgo.benefits.arch;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Validates that bounded contexts do not directly import classes from each other.
 *
 * <p>Cross-context communication must happen exclusively via Domain Events, per
 * rule #3 in {@code CLAUDE.md}. Direct calls (or imports) between {@code grant},
 * {@code wallet} and {@code redemption} collapse the boundary and invalidate the
 * DDD showcase.
 */
@AnalyzeClasses(
    packages = "com.wtaumaturgo.benefits",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class CrossContextIsolationTest {

    @ArchTest
    static final ArchRule grant_must_not_depend_on_wallet =
        noClasses()
            .that().resideInAPackage("..grant..")
            .should().dependOnClassesThat()
            .resideInAPackage("..wallet..")
            .because("Grant context must not directly import Wallet context classes");

    @ArchTest
    static final ArchRule grant_must_not_depend_on_redemption =
        noClasses()
            .that().resideInAPackage("..grant..")
            .should().dependOnClassesThat()
            .resideInAPackage("..redemption..")
            .because("Grant context must not directly import Redemption context classes");

    @ArchTest
    static final ArchRule wallet_must_not_depend_on_grant =
        noClasses()
            .that().resideInAPackage("..wallet..")
            .should().dependOnClassesThat()
            .resideInAPackage("..grant..")
            .because("Wallet context must not directly import Grant context classes");

    @ArchTest
    static final ArchRule wallet_must_not_depend_on_redemption =
        noClasses()
            .that().resideInAPackage("..wallet..")
            .should().dependOnClassesThat()
            .resideInAPackage("..redemption..")
            .because("Wallet context must not directly import Redemption context classes");

    @ArchTest
    static final ArchRule redemption_must_not_depend_on_grant =
        noClasses()
            .that().resideInAPackage("..redemption..")
            .should().dependOnClassesThat()
            .resideInAPackage("..grant..")
            .because("Redemption context must not directly import Grant context classes");

    @ArchTest
    static final ArchRule redemption_must_not_depend_on_wallet =
        noClasses()
            .that().resideInAPackage("..redemption..")
            .should().dependOnClassesThat()
            .resideInAPackage("..wallet..")
            .because("Redemption context must not directly import Wallet context classes");
}
