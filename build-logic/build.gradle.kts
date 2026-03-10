plugins {
    `kotlin-dsl`
}

dependencies {
    val libs = versionCatalogs.named("libs")
    compileOnly("com.android.tools.build:gradle:${libs.findVersion("agp").get()}")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.findVersion("kotlin").get()}")
    compileOnly("com.google.dagger:hilt-android-gradle-plugin:${libs.findVersion("hilt").get()}")
    compileOnly("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${libs.findVersion("ksp").get()}")
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "rostry.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "rostry.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "rostry.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidFeature") {
            id = "rostry.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "rostry.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
    }
}
