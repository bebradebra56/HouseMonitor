package com.housemo.monisto.data.local.dao

import androidx.room.*
import com.housemo.monisto.data.local.entity.Measurement
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Query("SELECT * FROM measurements WHERE issueId = :issueId ORDER BY measuredAt DESC")
    fun getMeasurementsByIssue(issueId: Long): Flow<List<Measurement>>

    @Query("SELECT * FROM measurements WHERE id = :id")
    fun getMeasurementById(id: Long): Flow<Measurement?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: Measurement): Long

    @Update
    suspend fun updateMeasurement(measurement: Measurement)

    @Delete
    suspend fun deleteMeasurement(measurement: Measurement)
}
