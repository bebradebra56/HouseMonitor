package com.housemo.monisto.data.local.dao

import androidx.room.*
import com.housemo.monisto.data.local.entity.Inspection
import kotlinx.coroutines.flow.Flow

@Dao
interface InspectionDao {
    @Query("SELECT * FROM inspections ORDER BY date DESC")
    fun getAllInspections(): Flow<List<Inspection>>

    @Query("SELECT * FROM inspections WHERE buildingId = :buildingId ORDER BY date DESC")
    fun getInspectionsByBuilding(buildingId: Long): Flow<List<Inspection>>

    @Query("SELECT * FROM inspections WHERE id = :id")
    fun getInspectionById(id: Long): Flow<Inspection?>

    @Query("SELECT * FROM inspections WHERE id = :id")
    suspend fun getInspectionByIdOnce(id: Long): Inspection?

    @Query("SELECT COUNT(*) FROM inspections")
    fun getInspectionCount(): Flow<Int>

    @Query("SELECT * FROM inspections ORDER BY date DESC LIMIT 5")
    fun getRecentInspections(): Flow<List<Inspection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(inspection: Inspection): Long

    @Update
    suspend fun updateInspection(inspection: Inspection)

    @Delete
    suspend fun deleteInspection(inspection: Inspection)
}
