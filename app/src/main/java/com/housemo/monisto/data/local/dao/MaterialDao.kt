package com.housemo.monisto.data.local.dao

import androidx.room.*
import com.housemo.monisto.data.local.entity.Material
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    @Query("SELECT * FROM materials ORDER BY name ASC")
    fun getAllMaterials(): Flow<List<Material>>

    @Query("SELECT * FROM materials WHERE type = :type ORDER BY name ASC")
    fun getMaterialsByType(type: String): Flow<List<Material>>

    @Query("SELECT * FROM materials WHERE id = :id")
    fun getMaterialById(id: Long): Flow<Material?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: Material): Long

    @Update
    suspend fun updateMaterial(material: Material)

    @Delete
    suspend fun deleteMaterial(material: Material)
}
