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

# Hilt generated components and entry points
-keep class **_HiltComponents { *; }
-keep class **_HiltModules { *; }
-keep class dagger.hilt.internal.generated.** { *; }

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

# Razorpay SDK
-keep class com.razorpay.** { *; }
-dontwarn com.razorpay.**

# WorkManager
-keep class androidx.work.impl.WorkDatabase_* { *; }
-dontwarn androidx.work.**

# Timber
-keep class timber.log.Timber { *; }
-dontwarn timber.log.**

# Keep Kotlin coroutines debug metadata
-keepclassmembers class kotlinx.coroutines.debug.internal.DebugProbesImpl { *; }

# Kotlin/coroutines general keeps
-dontwarn kotlin.**
-dontwarn kotlinx.coroutines.**
-keep class kotlin.Metadata { *; }
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlinx.coroutines.** { *; }

# Retain data/sealed classes' componentN and copy for safe reflection usages
-keepclassmembers class ** {
    kotlin.Metadata *;
}
-keepclassmembers class ** extends kotlin.Enum { *; }
-keepclassmembers class ** {  
    <methods>; 
}

# If using Room (KSP)
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Jetpack Compose
-keep class androidx.compose.** { *; }
-keep @androidx.compose.runtime.Composable class * { *; }
-keepclassmembers class * { @androidx.compose.runtime.Composable <methods>; }
-dontwarn androidx.compose.**

# Compose Preview providers (if any)
-keep class androidx.compose.ui.tooling.preview.PreviewParameterProvider { *; }
-keep class ** implements androidx.compose.ui.tooling.preview.PreviewParameterProvider { *; }

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve useful attributes for better stack traces and Kotlin metadata
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,*Annotation*,LocalVariableTable,LocalVariableTypeTable
-keepattributes *Annotation*

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile