plugins {
    `kotlin-dsl`
}

dependencies {
    val libs = versionCatalogs.named("libs")
    compileOnly("com.android.tools.build:gradle:${libs.findVersion("agp").get()}")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.findVersion("kotlin").get()}")
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
    }
}
