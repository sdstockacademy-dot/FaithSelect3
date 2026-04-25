# ─── FaithSelect ProGuard / R8 Rules ─────────────────────────────────────────

# Keep Kotlin metadata for reflection
-keepattributes *Annotation*, InnerClasses, Signature, Exceptions
-keepattributes EnclosingMethod

# ─── Firebase Firestore ───────────────────────────────────────────────────────
# Keep all data model classes used with toObject()
-keep class com.faithselect.domain.model.** { *; }
-keep class com.faithselect.data.remote.models.** { *; }

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# ─── Hilt ─────────────────────────────────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * extends android.app.Application

# ─── Room ─────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# ─── Google Play Billing ──────────────────────────────────────────────────────
-keep class com.android.billingclient.** { *; }

# ─── Media3 / ExoPlayer ───────────────────────────────────────────────────────
-keep class androidx.media3.** { *; }

# ─── Coroutines ───────────────────────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# ─── Kotlin serialization ─────────────────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ─── Compose ──────────────────────────────────────────────────────────────────
-keep class androidx.compose.** { *; }

# ─── General ──────────────────────────────────────────────────────────────────
-keepclassmembers enum * { public static **[] values(); public static ** valueOf(java.lang.String); }
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
