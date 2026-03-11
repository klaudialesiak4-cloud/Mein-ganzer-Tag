-keep class com.vitaltracker.app.data.db.** { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
