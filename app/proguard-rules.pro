# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Hilt
-keep class com.rio.rostry.di.** { *; }
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-dontwarn dagger.internal.**
-dontwarn javax.annotation.**

# Room
-keep class androidx.room.** { *; }
-keep class com.rio.rostry.data.local.** { *; }
-keep class com.rio.rostry.data.model.** { *; }
-keep @androidx.room.Entity class *

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlin.**

# Coroutines
-dontwarn kotlinx.coroutines.flow.**
-dontwarn kotlinx.coroutines.internal.**

# Timber
-keep class timber.log.** { *; }

# WorkManager
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# Compose
-keep class androidx.compose.** { *; }
-keep class androidx.navigation.** { *; }
-dontwarn androidx.compose.**
-dontwarn androidx.navigation.**

# ViewModel
-keep class androidx.lifecycle.ViewModel { *; }
-keep class androidx.lifecycle.ViewModelProvider { *; }