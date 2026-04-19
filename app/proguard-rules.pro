# Add project specific ProGuard rules here.
-keepclassmembers class * {
  *** **(kotlin.coroutines.Continuation);
}

# Keep Filament classes
-keep class com.google.android.filament.** { *; }
-keepclassmembers class com.google.android.filament.** { *; }

# Keep GSON classes
-keep class com.google.gson.** { *; }
-keepclassmembers class com.google.gson.** { *; }

# Keep Timber logging
-keep class timber.log.** { *; }

# Keep game engine classes
-keep class com.neogame.psp.emulator.** { *; }
-keep class com.neogame.psp.renderer.** { *; }
-keep class com.neogame.psp.ui.** { *; }