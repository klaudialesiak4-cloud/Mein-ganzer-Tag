package com.vitaltracker.app.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VitalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: VitalEntry): Long

    @Update
    suspend fun update(entry: VitalEntry)

    @Query("SELECT * FROM vital_entries ORDER BY date DESC")
    fun getAllDesc(): LiveData<List<VitalEntry>>

    @Query("SELECT * FROM vital_entries ORDER BY date ASC")
    fun getAllAsc(): LiveData<List<VitalEntry>>

    @Query("DELETE FROM vital_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}
