package com.vitaltracker.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.vitaltracker.app.data.db.VitalDatabase
import com.vitaltracker.app.data.db.VitalEntry
import com.vitaltracker.app.data.repository.VitalRepository
import kotlinx.coroutines.launch

class VitalViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = VitalRepository(VitalDatabase.get(app).vitalDao())
    private val prefs = app.getSharedPreferences("vital_prefs", Context.MODE_PRIVATE)

    val allDesc = repo.allDesc
    val allAsc  = repo.allAsc

    fun insert(e: VitalEntry)    = viewModelScope.launch { repo.insert(e) }
    fun deleteById(id: Long)     = viewModelScope.launch { repo.deleteById(id) }

    var userAge:      Int     get() = prefs.getInt("age", 40);           set(v) { prefs.edit().putInt("age", v).apply() }
    var userGender:   String  get() = prefs.getString("gender","m")?:"m"; set(v) { prefs.edit().putString("gender",v).apply() }
    var userHeightCm: Float   get() = prefs.getFloat("height",170f);      set(v) { prefs.edit().putFloat("height",v).apply() }
    var isAthlete:    Boolean get() = prefs.getBoolean("athlete",false);   set(v) { prefs.edit().putBoolean("athlete",v).apply() }
    var userName:     String  get() = prefs.getString("name","")?:"";      set(v) { prefs.edit().putString("name",v).apply() }
}
