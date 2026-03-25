package com.housemo.monisto.data.local.dao

import androidx.room.*
import com.housemo.monisto.data.local.entity.Room
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {
    @Query("SELECT * FROM rooms WHERE floorId = :floorId ORDER BY name ASC")
    fun getRoomsByFloor(floorId: Long): Flow<List<Room>>

    @Query("SELECT * FROM rooms WHERE id = :id")
    fun getRoomById(id: Long): Flow<Room?>

    @Query("SELECT COUNT(*) FROM rooms WHERE floorId = :floorId")
    fun getRoomCountForFloor(floorId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: Room): Long

    @Update
    suspend fun updateRoom(room: Room)

    @Delete
    suspend fun deleteRoom(room: Room)
}
