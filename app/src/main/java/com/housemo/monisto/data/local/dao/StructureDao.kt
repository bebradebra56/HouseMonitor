package com.housemo.monisto.data.local.dao

import androidx.room.*
import com.housemo.monisto.data.local.entity.Structure
import kotlinx.coroutines.flow.Flow

@Dao
interface StructureDao {
    @Query("SELECT * FROM structures WHERE roomId = :roomId ORDER BY type ASC")
    fun getStructuresByRoom(roomId: Long): Flow<List<Structure>>

    @Query("SELECT * FROM structures WHERE id = :id")
    fun getStructureById(id: Long): Flow<Structure?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStructure(structure: Structure): Long

    @Update
    suspend fun updateStructure(structure: Structure)

    @Delete
    suspend fun deleteStructure(structure: Structure)
}
