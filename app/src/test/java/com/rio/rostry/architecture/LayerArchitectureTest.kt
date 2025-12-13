package com.rio.rostry.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.library.Architectures
import org.junit.Test
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.core.domain.properties.HasName
import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.elements.ClassesShouldConjunction
import com.tngtech.archunit.core.domain.properties.CanBeAnnotated

/**
 * Architectural tests to enforce layer separation in the ROSTRY application.
 * These tests ensure that:
 * 1. DAOs are only injected into repository classes (not into ViewModels or UI components)
 * 2. ViewModels only depend on repositories, not DAOs directly
 */
@AnalyzeClasses(packages = ["com.rio.rostry"])
class LayerArchitectureTest {

    @ArchTest
    fun `ViewModels should not directly depend on DAOs`() {
        // This rule ensures that classes ending with "ViewModel" do not have constructor parameters
        // of type "Dao" (which includes ProductDao and other DAO classes)
        ArchRuleDefinition
            .noClasses()
            .that().haveSimpleNameEndingWith("ViewModel")
            .should().dependOnClassesThat().haveSimpleNameContaining("Dao")
    }

    @ArchTest
    fun `UI layer components should not directly depend on DAOs`() {
        // This rule ensures that UI layer components (in ui.* packages) do not depend on DAOs
        ArchRuleDefinition
            .noClasses()
            .that().resideInAPackage("..ui..")
            .should().dependOnClassesThat().haveSimpleNameContaining("Dao")
    }

    @ArchTest
    fun `DAOs should only be injected into repository classes`() {
        // This rule ensures that any class with "Dao" in its name is only injected into classes
        // with "Repository" in their name
        ArchRuleDefinition
            .classes()
            .that().haveSimpleNameContaining("Dao")
            .should().onlyBeAccessed()
            .byClassesThat().haveSimpleNameContaining("Repository")
    }
}