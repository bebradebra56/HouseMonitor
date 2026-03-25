package com.housemo.monisto.data.local.dao

import androidx.room.*
import com.housemo.monisto.data.local.entity.Floor
import kotlinx.coroutines.flow.Flow

@Dao
interface FloorDao {
    @Query("SELECT * FROM floors WHERE buildingId = :buildingId ORDER BY level ASC")
    fun getFloorsByBuilding(buildingId: Long): Flow<List<Floor>>

    @Query("SELECT * FROM floors WHERE id = :id")
    fun getFloorById(id: Long): Flow<Floor?>

    @Query("SELECT COUNT(*) FROM floors WHERE buildingId = :buildingId")
    fun getFloorCountForBuilding(buildingId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFloor(floor: Floor): Long

    @Update
    suspend fun updateFloor(floor: Floor)

    @Delete
    suspend fun deleteFloor(floor: Floor)
}
