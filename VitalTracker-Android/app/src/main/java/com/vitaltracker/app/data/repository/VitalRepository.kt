package com.vitaltracker.app.data.repository

import androidx.lifecycle.LiveData
import com.vitaltracker.app.data.db.VitalDao
import com.vitaltracker.app.data.db.VitalEntry

class VitalRepository(private val dao: VitalDao) {
    val allDesc: LiveData<List<VitalEntry>> = dao.getAllDesc()
    val allAsc:  LiveData<List<VitalEntry>> = dao.getAllAsc()
    suspend fun insert(e: VitalEntry) = dao.insert(e)
    suspend fun deleteById(id: Long)  = dao.deleteById(id)
}
