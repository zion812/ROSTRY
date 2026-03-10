import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for feature modules.
 *
 * Applies:
 * - rostry.android.library.compose (Android Library + Kotlin + Compose compiler)
 * - rostry.android.hilt (Hilt DI + KSP)
 *
 * Adds common feature-module dependencies:
 * - Compose BOM + UI/Material3/Tooling/Navigation
 * - core:designsystem, core:navigation, core:common, core:model
 * - Hilt Navigation Compose
 * - Lifecycle ViewModel Compose
 * - Testing baseline
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(AndroidLibraryComposeConventionPlugin::class.java)
            pluginManager.apply(AndroidHiltConventionPlugin::class.java)

            val libs = extensions.getByType(
                org.gradle.api.artifacts.VersionCatalogsExtension::class.java
            ).named("libs")

            dependencies {
                // Core modules every feature needs
                add("implementation", project(":core:designsystem"))
                add("implementation", project(":core:navigation"))
                add("implementation", project(":core:common"))
                add("implementation", project(":core:model"))
                add("implementation", project(":core:database")) // Added for entity access

                // Domain modules
                add("implementation", project(":domain:account"))
                add("implementation", project(":domain:commerce"))
                add("implementation", project(":domain:farm"))
                add("implementation", project(":domain:monitoring"))
                add("implementation", project(":domain:social"))
                add("implementation", project(":domain:admin"))

                // Data modules (temporary for compatibility adapters and direct repos)
                add("implementation", project(":data:account"))
                add("implementation", project(":data:commerce"))
                add("implementation", project(":data:farm"))
                add("implementation", project(":data:monitoring"))
                add("implementation", project(":data:social"))
                add("implementation", project(":data:admin"))

                // Compose BOM + core libraries
                add("implementation", platform(libs.findLibrary("androidx-compose-bom").get()))
                add("implementation", libs.findLibrary("androidx-compose-ui").get())
                add("implementation", libs.findLibrary("androidx-compose-ui-graphics").get())
                add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
                add("implementation", libs.findLibrary("androidx-compose-material3").get())
                add("implementation", libs.findLibrary("androidx-navigation-compose").get())

                // Hilt + Navigation integration
                add("implementation", libs.findLibrary("hilt-navigation-compose").get())

                // ViewModel for Compose
                add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-runtime-compose").get())

                // Debug tooling
                add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())

                // Testing baseline
                add("testImplementation", project(":core:testing"))
                add("testImplementation", libs.findLibrary("junit").get())
            }
        }
    }
}
