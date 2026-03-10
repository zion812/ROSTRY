package com.rio.rostry.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.Test

/**
 * Phase 0 Guardrails: Architecture tests enforcing module boundaries and dependency rules.
 * These tests will initially fail, exposing violations to migrate incrementally.
 */
class ModularArchitectureTest {

    private val importedClasses = ClassFileImporter()
        .withImportOption(ImportOption.DoNotIncludeTests())
        .importPackages("com.rio.rostry")

    @Test
    fun `feature modules should not depend on data modules`() {
        noClasses()
            .that().resideInAPackage("..feature..")
            .should().dependOnClassesThat().resideInAPackage("..data..")
            .because("Feature modules should depend on domain contracts, not implementations")
            .check(importedClasses)
    }

    @Test
    fun `domain modules should not depend on Android framework`() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("android..", "androidx..")
            .because("Domain layer should be framework-independent")
            .allowEmptyShould(true)
            .check(importedClasses)
    }

    @Test
    fun `repositories should implement domain interfaces`() {
        classes()
            .that().haveSimpleNameEndingWith("RepositoryImpl")
            .and().resideInAPackage("..data..")
            .should().implement(com.tngtech.archunit.base.DescribedPredicate.describe("a Repository interface") {
                it.name.endsWith("Repository")
            })
            .andShould().notBeInterfaces()
            .because("Data implementations must implement domain repository interfaces")
            .check(importedClasses)
    }

    @Test
    fun `app module should not contain ViewModels`() {
        noClasses()
            .that().resideInAPackage("..app..")
            .should().haveSimpleNameEndingWith("ViewModel")
            .because("ViewModels should be in feature modules")
            .check(importedClasses)
    }

    @Test
    fun `app module should not depend on data or domain modules (to be enforced)`() {
        // This rule documents the target state. It will likely fail initially and guide migration.
        noClasses()
            .that().resideInAPackage("..app..")
            .should().dependOnClassesThat().resideInAnyPackage("..data..", "..domain..")
            .because("App shell must be thin and depend only on feature and core modules")
            .check(importedClasses)
    }
}
