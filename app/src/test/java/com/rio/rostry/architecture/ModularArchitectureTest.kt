package com.rio.rostry.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.Before
import org.junit.Test
import org.junit.Ignore

/**
 * Architecture tests enforcing module boundaries and dependency rules.
 * 
 * Feature: end-to-end-modularization
 * Phase: 0 - Guardrails First
 * Requirements: 1.1, 1.2, 1.3, 1.4
 */
@Ignore("Phase 1 modularization WIP - to be re-enabled when files are moved")
class ModularArchitectureTest {
    
    private lateinit var classes: JavaClasses
    
    @Before
    fun setup() {
        classes = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .withImportOption(ImportOption { location -> !location.contains("/build/") }) // Exclude generated code
            .importPackages("com.rio.rostry")
    }
    
    /**
     * Property 3: Feature Module Dependency Constraint
     * Validates: Requirements 1.1
     * 
     * For any feature module, all of its dependencies should be either domain modules 
     * or core modules, never data modules.
     */
    @Test
    fun `feature modules should only depend on domain and core modules`() {
        noClasses()
            .that().resideInAPackage("..feature..")
            .should().dependOnClassesThat().resideInAPackage("..data..")
            .because("Feature modules should depend on domain contracts, not implementations")
            .check(classes)
    }
    
    /**
     * Property 4: Domain Module Isolation
     * Validates: Requirements 1.2
     * 
     * For any domain module, it should not depend on any data module or feature module.
     */
    @Test
    fun `domain modules should not depend on data or feature modules`() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..data..", "..feature..")
            .because("Domain layer should be independent of implementation and presentation layers")
            .check(classes)
    }
    
    /**
     * Property 5: Data Module Dependency Constraint
     * Validates: Requirements 1.3
     * 
     * For any data module, all of its dependencies should be either domain modules 
     * or core modules, never feature modules.
     */
    @Test
    fun `data modules should only depend on domain and core modules`() {
        noClasses()
            .that().resideInAPackage("..data..")
            .should().dependOnClassesThat().resideInAPackage("..feature..")
            .because("Data modules should not depend on presentation layer")
            .check(classes)
    }
    
    /**
     * Property 6: Domain Framework Independence
     * Validates: Requirements 4.4
     * 
     * For any class in a domain module, it should not import Android framework classes 
     * (android.* or androidx.*) except for annotation packages.
     */
    @Test
    fun `domain modules should not depend on Android framework`() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("android..", "androidx..")
            .because("Domain layer should be framework-independent")
            .allowEmptyShould(true)
            .check(classes)
    }
    
    /**
     * Property 1: App Shell Isolation (partial)
     * Validates: Requirements 1.4, 3.2
     * 
     * For any class in the app module, that class should not be a repository implementation.
     */
    @Test
    fun `app module should not contain repositories`() {
        noClasses()
            .that().resideInAPackage("..app..")
            .should().haveSimpleNameEndingWith("Repository")
            .orShould().haveSimpleNameEndingWith("RepositoryImpl")
            .because("Repositories should be in data modules")
            .check(classes)
    }
    
    /**
     * Property 1: App Shell Isolation (partial)
     * Validates: Requirements 1.4, 3.2
     * 
     * For any class in the app module, that class should not be a use case implementation.
     */
    @Test
    fun `app module should not contain use cases`() {
        noClasses()
            .that().resideInAPackage("..app..")
            .should().haveSimpleNameEndingWith("UseCase")
            .because("Use cases should be in domain modules")
            .check(classes)
    }
    
    /**
     * Property 8: Data Implementation Completeness (partial)
     * Validates: Requirements 4.2
     * 
     * For any repository implementation in a data module, it should implement 
     * a repository interface.
     */
    @Test
    fun `repositories should implement domain interfaces`() {
        classes()
            .that().haveSimpleNameEndingWith("RepositoryImpl")
            .and().resideInAPackage("..data..")
            .should().implement(classNameMatching(".*Repository"))
            .andShould().notBeInterfaces()
            .because("Repository implementations should implement domain interfaces")
            .check(classes)
    }
    
    private fun classNameMatching(pattern: String) = 
        com.tngtech.archunit.base.DescribedPredicate.describe(
            "class name matching '$pattern'"
        ) { javaClass: com.tngtech.archunit.core.domain.JavaClass ->
            javaClass.name.matches(Regex(pattern))
        }
}
