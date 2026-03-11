package com.vitaltracker.app.data.repository

import androidx.lifecycle.LiveData
import com.vitaltracker.app.data.db.VitalDao
import com.vitaltracker.app.data.db.VitalEntry

class VitalRepository(private val dao: VitalDao) {

    val allEntriesDesc: LiveData<List<VitalEntry>> = dao.getAllEntries()
    val allEntriesAsc: LiveData<List<VitalEntry>> = dao.getAllEntriesAsc()

    suspend fun insert(entry: VitalEntry): Long = dao.insert(entry)

    suspend fun update(entry: VitalEntry) = dao.update(entry)

    suspend fun delete(entry: VitalEntry) = dao.delete(entry)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun getLatestEntry(): VitalEntry? = dao.getLatestEntry()

    suspend fun getEntryById(id: Long): VitalEntry? = dao.getEntryById(id)
}
