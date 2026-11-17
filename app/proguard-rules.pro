# Add project-specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/gui/developing/optimize/proguard-files.txt

# --- Kotlinx Serialization ---
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers @kotlinx.serialization.Serializable class * {
    *** Companion;
    *** serializer(...);
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}

# --- App Data Models ---
-keep class com.chavesdev.rollit.model.** { *; }

# --- ViewModels & UI ---
-keep class com.chavesdev.rollit.viewmodel.** { *; }
-keep class com.chavesdev.rollit.ui.** { *; }

# --- Hardware, Sensors & Audio ---
-keep class com.chavesdev.rollit.hardware.** { *; }
-keep class * implements android.hardware.SensorEventListener {
    <methods>;
}

# --- Room & Database (if used) ---
-keepclassmembers,allowobfuscation class * extends androidx.room.RoomDatabase {
    *int *;
    <init>();
}

# --- Moshi & Retrofit (if used) ---
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.squareup.moshi.**
