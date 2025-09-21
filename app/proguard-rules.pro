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

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keepclassmembers class kotlin.coroutines.jvm.internal.BaseContinuationImpl {
    <fields>;
}
-keepclassmembers class kotlin.coroutines.jvm.internal.DebugProbesKt {
    <methods>;
}
-keepnames class kotlin.coroutines.CoroutineDebuggingProbesKt {}


# Hilt
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    @javax.inject.Inject <init>(...);
}
-keep class * implements dagger.hilt.InstallInProviderEntryPoint { *; }
-keep class * implements dagger.hilt.EntryPoint { *; }
-keep class dagger.hilt.internal.aggregatedroot.codegen.*
-keep class dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
-keep class dagger.hilt.android.internal.managers.ActivityComponentManager
-keep class dagger.hilt.android.internal.managers.BroadcastReceiverComponentManager
-keep class dagger.hilt.android.internal.managers.FragmentComponentManager
-keep class dagger.hilt.android.internal.managers.ServiceComponentManager
-keep class dagger.hilt.android.internal.managers.ViewComponentManager
-keep class dagger.hilt.android.internal.builders.*
-keep class dagger.hilt.android.internal.modules.*
-keep class dagger.hilt.android.HiltAndroidApp
-keep @dagger.hilt.android.HiltAndroidApp class * { <init>(); }
-keep @dagger.hilt.components.SingletonComponent interface *
-keep @dagger.hilt.DefineComponent interface *
-keep @dagger.hilt.android.EarlyEntryPoint interface * { <methods>; }
-keepclassmembers @dagger.hilt.android.AndroidEntryPoint class * {
    @javax.inject.Inject <fields>;
    @javax.inject.Inject <methods>;
}
-keepclassmembers @dagger.Module class * {
    @dagger.Provides <methods>;
    @dagger.Binds <methods>;
}
-keepclassmembers @dagger.hilt.codegen.OriginatingElement class * { *; }
-keepclassmembers @dagger.hilt.InstallIn class * { *; }

# Room
-keep class androidx.room.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static *** createOpenHelper(...);
    public static *** createOpenHelper(...);
    public static *** createOpenHelper(...);
}
-keepclassmembers public abstract class * extends androidx.room.RoomDatabase {
    public abstract *** *(...);
}
-keepclassmembers class * extends androidx.room.Entity { *; }
-keepclassmembers class * implements androidx.room.Dao { *; }
-keepclassmembers class * extends androidx.room.TypeConverter { *; }
-keepclassmembers class * extends androidx.room.TypeConverters { *; }

# Gson (used by Room TypeConverter and potentially elsewhere)
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class com.google.gson.Gson { *; }
-keep class com.google.gson.stream.** { *; }
# For GSON Interface Deserialization
-keep class sun.misc.Unsafe { *; }
-keep interface com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.TypeAdapterFactory
-keep interface com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonSerializer
-keep interface com.google.gson.JsonDeserializer
-keep class * implements com.google.gson.JsonDeserializer

# Firebase / Google Play Services
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.firebase.** { *; }
-keepnames class com.google.android.gms.measurement.AppMeasurementReceiver
-keepnames class com.google.android.gms.measurement.AppMeasurementService
-keepnames class com.google.android.gms.measurement.AppMeasurementJobService
-keepnames class com.google.firebase.provider.FirebaseInitProvider
-keep class com.google.android.gms.auth.api.signin.internal.* # For Google Sign-In
-keep class com.google.android.gms.tasks.** { *; }

# Suppress warnings for Firebase KTX
-dontwarn com.google.firebase.ktx.Firebase

# For Parcelables, keep constructors and CREATOR field
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements android.os.Parcelable {
    public <init>(android.os.Parcel);
}

# Keep custom Views, ViewGroups, and their constructors if they are used from XML
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Keep enums used in data classes and elsewhere
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep data classes (often used with Room, Retrofit, Gson)
-keepclassmembers class kotlin.Metadata { *; }
-keep class * extends kotlin.jvm.internal.Lambda
-keepclassmembers class **$WhenMappings { <fields>; }
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, InnerClasses, EnclosingMethod, Signature, Exceptions

# If you use Jetpack Compose
-keepclassmembers class * { @androidx.compose.runtime.Composable <methods>; }
-keepclassmembers class * { @androidx.compose.runtime.Composable <fields>; }
-keepattributes *Annotation*
-keepclassmembers class androidx.compose.ui.tooling.preview.ComposeViewAdapter { <init>(...); }

# Timber
-dontwarn timber.log.**
-keep class timber.log.Timber$Tree { *; }
-keep class timber.log.Timber$DebugTree { *; }

# General Kotlin
-keepnames class kotlin.Unit
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepnames class kotlin.jvm.functions.**
-keepnames class kotlin.collections.CollectionsKt
-keepclassmembernames class kotlin.coroutines.jvm.internal.DebugMetadataKt {
    <methods>;
}

# Add rules for any other specific libraries or reflective operations you might use.
