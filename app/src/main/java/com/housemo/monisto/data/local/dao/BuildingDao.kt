package com.housemo.monisto.data.local.dao

import androidx.room.*
import com.housemo.monisto.data.local.entity.Building
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildingDao {
    @Query("SELECT * FROM buildings ORDER BY createdAt DESC")
    fun getAllBuildings(): Flow<List<Building>>

    @Query("SELECT * FROM buildings WHERE id = :id")
    fun getBuildingById(id: Long): Flow<Building?>

    @Query("SELECT COUNT(*) FROM buildings")
    fun getBuildingCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuilding(building: Building): Long

    @Update
    suspend fun updateBuilding(building: Building)

    @Delete
    suspend fun deleteBuilding(building: Building)

    @Query("UPDATE buildings SET conditionScore = :score WHERE id = :id")
    suspend fun updateConditionScore(id: Long, score: Float)
}
