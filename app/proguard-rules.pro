# ProGuard / R8 rules for Lipika

# Keep JNI / Rust NativeEngine methods
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class io.lipika.feature.ime.NativeEngine {
    *;
}

# Keep Activities, Services, and components launched by intent or reflection
-keep class io.lipika.app.MainActivity { *; }
-keep class io.lipika.feature.ime.LipikaService { *; }
-keep class androidx.emoji2.emojipicker.BundledEmojiListLoader { *; }

# Keep Kotlinx Serialization data models
-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.SerializationKt

-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class io.lipika.common.model.** { *; }
-keep class io.lipika.theme.** { *; }
-keep class io.lipika.data.layout.** { *; }

