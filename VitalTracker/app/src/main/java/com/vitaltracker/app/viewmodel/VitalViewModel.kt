package com.vitaltracker.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.vitaltracker.app.data.db.VitalDatabase
import com.vitaltracker.app.data.db.VitalEntry
import com.vitaltracker.app.data.repository.VitalRepository
import kotlinx.coroutines.launch

class VitalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VitalRepository
    val allEntriesDesc: LiveData<List<VitalEntry>>
    val allEntriesAsc: LiveData<List<VitalEntry>>

    private val prefs = application.getSharedPreferences("vital_prefs", Context.MODE_PRIVATE)

    init {
        val dao = VitalDatabase.getDatabase(application).vitalDao()
        repository = VitalRepository(dao)
        allEntriesDesc = repository.allEntriesDesc
        allEntriesAsc = repository.allEntriesAsc
    }

    fun insert(entry: VitalEntry) = viewModelScope.launch {
        repository.insert(entry)
    }

    fun update(entry: VitalEntry) = viewModelScope.launch {
        repository.update(entry)
    }

    fun deleteById(id: Long) = viewModelScope.launch {
        repository.deleteById(id)
    }

    // ---- Einstellungen (Preferences) ----

    var userAge: Int
        get() = prefs.getInt("age", 40)
        set(v) = prefs.edit().putInt("age", v).apply()

    var userGender: String
        get() = prefs.getString("gender", "m") ?: "m"
        set(v) = prefs.edit().putString("gender", v).apply()

    var userHeightCm: Float
        get() = prefs.getFloat("height_cm", 170f)
        set(v) = prefs.edit().putFloat("height_cm", v).apply()

    var isAthlete: Boolean
        get() = prefs.getBoolean("is_athlete", false)
        set(v) = prefs.edit().putBoolean("is_athlete", v).apply()

    var userName: String
        get() = prefs.getString("user_name", "") ?: ""
        set(v) = prefs.edit().putString("user_name", v).apply()
}
