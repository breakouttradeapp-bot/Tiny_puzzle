# Add project specific ProGuard rules here.

# Keep Compose runtime classes
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }

# Keep Google Play Billing
-keep class com.android.billingclient.** { *; }

# Keep Google Mobile Ads
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep data classes
-keep class com.tinygenius.data.model.** { *; }

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Keep Parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep R8 rules for Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }
