package com.vitaltracker.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VitalEntry::class], version = 1, exportSchema = false)
abstract class VitalDatabase : RoomDatabase() {
    abstract fun vitalDao(): VitalDao

    companion object {
        @Volatile private var INSTANCE: VitalDatabase? = null
        fun get(context: Context): VitalDatabase = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(context.applicationContext, VitalDatabase::class.java, "vital_db")
                .build().also { INSTANCE = it }
        }
    }
}
