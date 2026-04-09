# Keep model classes
-keep class com.example.botwast.MessageRule { *; }
-keep class com.example.botwast.Contact { *; }
-keep class com.example.botwast.Statistics { *; }
-keep class com.example.botwast.ContactStat { *; }

# Keep services
-keep class com.example.botwast.WhatsAppListener { *; }
-keep class com.example.botwast.BootReceiver { *; }

# Gson
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Coroutines
-keep class kotlinx.coroutines.** { *; }

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlin.jvm.internal.** { *; }