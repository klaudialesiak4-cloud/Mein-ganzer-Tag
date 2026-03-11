package com.vitaltracker.app.util

/**
 * Berechnet die Normalwerte basierend auf persönlichen Daten.
 * Quellen: WHO, Deutsche Hochdruckliga, DGK
 */
object NormalValues {

    data class BloodPressureRange(
        val systolicMin: Int,
        val systolicMax: Int,
        val diastolicMin: Int,
        val diastolicMax: Int
    )

    data class WeightRange(
        val minKg: Float,
        val maxKg: Float,
        val bmiMin: Float = 18.5f,
        val bmiMax: Float = 24.9f
    )

    /**
     * Blutdruck-Normalwerte nach Alter und Geschlecht
     * Quelle: Deutsche Hochdruckliga
     */
    fun bloodPressureRange(ageYears: Int, gender: String): BloodPressureRange {
        return when {
            ageYears < 40 -> BloodPressureRange(90, 120, 60, 80)
            ageYears < 50 -> BloodPressureRange(90, 125, 60, 82)
            ageYears < 60 -> BloodPressureRange(90, 130, 60, 85)
            ageYears < 70 -> BloodPressureRange(90, 135, 60, 85)
            else           -> BloodPressureRange(90, 140, 60, 90)
        }
    }

    /**
     * Puls-Normalwerte: 60–100 bpm (Erwachsene)
     * Athleten können bis zu 40 bpm haben.
     */
    fun pulseRange(ageYears: Int, isAthlete: Boolean): Pair<Int, Int> {
        val lower = if (isAthlete) 40 else 60
        return Pair(lower, 100)
    }

    /**
     * Sauerstoffsättigung: normal ≥ 95 %
     */
    fun oxygenSaturationRange(): Pair<Float, Float> = Pair(95f, 100f)

    /**
     * Gewicht-Normalbereich über BMI (kg)
     * BMI = Gewicht / (Größe in m)²
     * Normalbereich: BMI 18,5 – 24,9
     */
    fun weightRange(heightCm: Float): WeightRange {
        val heightM = heightCm / 100f
        val minKg = 18.5f * heightM * heightM
        val maxKg = 24.9f * heightM * heightM
        return WeightRange(minKg, maxKg)
    }

    fun bmi(weightKg: Float, heightCm: Float): Float {
        val h = heightCm / 100f
        return weightKg / (h * h)
    }

    fun bmiCategory(bmi: Float): String = when {
        bmi < 18.5f -> "Untergewicht"
        bmi < 25.0f -> "Normalgewicht"
        bmi < 30.0f -> "Übergewicht"
        else        -> "Adipositas"
    }
}
