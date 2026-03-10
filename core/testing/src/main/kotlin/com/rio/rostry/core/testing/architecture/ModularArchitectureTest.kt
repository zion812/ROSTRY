package com.rio.rostry.core.testing.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import org.junit.Before
import org.junit.Test

/**
 * Architecture tests enforcing module boundaries and dependency rules.
 *
 * Phase 0: Guardrails First
 * Requirement 1: Establish Architectural Guardrails
 */
class ModularArchitectureTest {

    private lateinit var classes: JavaClasses

    @Before
    fun setup() {
        classes = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages("com.rio.rostry")
    }

    @Test
    fun `feature modules should only depend on domain and core modules`() {
        com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses()
            .that().resideInAPackage("..feature..")
            .and().doNotHaveSimpleName("NavigationProvider")
            .should().dependOnClassesThat().resideInAPackage("..data..")
            .check(classes)
    }

    @Test
    fun `domain modules should not depend on data or feature modules`() {
        com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..data..", "..feature..")
            .check(classes)
    }

    @Test
    fun `data modules should only depend on domain and core modules`() {
        com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses()
            .that().resideInAPackage("..data..")
            .should().dependOnClassesThat().resideInAPackage("..feature..")
            .check(classes)
    }

    @Test
    fun `domain modules should be framework-independent`() {
        com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("android..", "androidx..")
            .because("Domain layer should be framework-independent")
            .check(classes)
    }

    @Test
    fun `app module should not contain repositories`() {
        com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses()
            .that().resideInAPackage("com.rio.rostry.data.repository..")
            .and().resideInAPackage("..app..")
            .should().haveSimpleNameEndingWith("Repository")
            .orShould().haveSimpleNameEndingWith("RepositoryImpl")
            .check(classes)
    }

    @Test
    fun `app module should not contain use cases`() {
        com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses()
            .that().resideInAPackage("..app..")
            .should().haveSimpleNameEndingWith("UseCase")
            .check(classes)
    }

    @Test
    fun `repositories should implement domain interfaces`() {
        com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes()
            .that().haveSimpleNameEndingWith("RepositoryImpl")
            .should().implement(com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleNameEndingWith("Repository"))
            .check(classes)
    }

    @Test
    fun `core navigation module should contain NavigationRegistry`() {
        com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes()
            .that().haveSimpleName("NavigationRegistryImpl")
            .should().resideInAPackage("..core.navigation..")
            .check(classes)
    }

    @Test
    fun `navigation providers should be in feature modules`() {
        com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes()
            .that().haveSimpleNameEndingWith("NavigationProvider")
            .and().doNotHaveSimpleName("FakeNavigationProvider")
            .should().resideInAnyPackage("..feature..", "..ui..navigation")
            .check(classes)
    }
}
