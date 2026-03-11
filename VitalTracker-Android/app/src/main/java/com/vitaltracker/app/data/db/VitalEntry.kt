package com.vitaltracker.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vital_entries")
data class VitalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val weightKg: Float,
    val bloodPressureSystolic: Int,
    val bloodPressureDiastolic: Int,
    val pulseBeatsPerMinute: Int,
    val oxygenSaturationPercent: Float,
    val notes: String = ""
)
