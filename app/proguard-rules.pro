-dontwarn javax.annotation.**
-keep class com.liquidglass.musicplayer.data.model.** { *; }
-keep class com.liquidglass.musicplayer.data.remote.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
