package com.vitaltracker.app.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VitalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: VitalEntry): Long

    @Update
    suspend fun update(entry: VitalEntry)

    @Delete
    suspend fun delete(entry: VitalEntry)

    @Query("SELECT * FROM vital_entries ORDER BY date DESC")
    fun getAllEntries(): LiveData<List<VitalEntry>>

    @Query("SELECT * FROM vital_entries ORDER BY date ASC")
    fun getAllEntriesAsc(): LiveData<List<VitalEntry>>

    @Query("SELECT * FROM vital_entries ORDER BY date DESC LIMIT 1")
    suspend fun getLatestEntry(): VitalEntry?

    @Query("SELECT * FROM vital_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): VitalEntry?

    @Query("DELETE FROM vital_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}
