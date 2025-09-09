# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep Hilt/Dagger generated code
-dontwarn dagger.hilt.internal.**
-keep class dagger.hilt.** { *; }
-keep class **_HiltModules_** { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltWrapper_* { *; }
-keep class androidx.hilt.WorkerFactoryModule { *; }
-dontwarn javax.inject.**

# Retrofit / Moshi
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keep class retrofit2.** { *; }
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class ** {
    @com.squareup.moshi.* <fields>;
}

# OkHttp
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# WorkManager
-keep class androidx.work.impl.WorkDatabase_* { *; }
-dontwarn androidx.work.**

# Timber
-keep class timber.log.Timber { *; }
-dontwarn timber.log.**

# Keep Kotlin coroutines debug metadata
-keepclassmembers class kotlinx.coroutines.debug.internal.DebugProbesImpl { *; }

# If using Room (KSP)
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

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