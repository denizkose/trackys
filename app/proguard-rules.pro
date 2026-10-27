# Retrofit 2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationExtension
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
# Захист WorkManager та його Worker-ів
-keep class androidx.work.** { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-keep class * extends androidx.work.Worker { *; }
-keepclassmembers class * extends androidx.work.WorkerParameters { *; }

# Захист стандартної бібліотеки Kotlin (текстові утиліти та рефлексія)
-dontwarn kotlin.text.**
-keep class kotlin.text.** { *; }
-keep class kotlin.jvm.internal.** { *; }