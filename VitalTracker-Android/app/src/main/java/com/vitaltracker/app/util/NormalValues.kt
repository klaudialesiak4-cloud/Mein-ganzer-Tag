package com.vitaltracker.app.util

object NormalValues {
    data class BpRange(val sysMin: Int, val sysMax: Int, val diaMin: Int, val diaMax: Int)

    fun bloodPressure(age: Int): BpRange = when {
        age < 40 -> BpRange(90, 120, 60, 80)
        age < 50 -> BpRange(90, 125, 60, 82)
        age < 60 -> BpRange(90, 130, 60, 85)
        age < 70 -> BpRange(90, 135, 60, 85)
        else     -> BpRange(90, 140, 60, 90)
    }

    fun pulse(athlete: Boolean)  = if (athlete) 40 to 100 else 60 to 100
    fun oxygen()                 = 95f to 100f
    fun weight(heightCm: Float)  = (18.5f * (heightCm/100).let{it*it}) to (24.9f * (heightCm/100).let{it*it})
    fun bmi(kg: Float, cm: Float) = kg / (cm/100).let { it * it }
    fun bmiCategory(b: Float) = when {
        b < 18.5f -> "Untergewicht"
        b < 25.0f -> "Normalgewicht"
        b < 30.0f -> "Übergewicht"
        else      -> "Adipositas"
    }
}
