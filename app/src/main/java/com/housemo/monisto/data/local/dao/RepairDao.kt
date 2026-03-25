package com.housemo.monisto.data.local.dao

import androidx.room.*
import com.housemo.monisto.data.local.entity.Repair
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairDao {
    @Query("SELECT * FROM repairs ORDER BY createdAt DESC")
    fun getAllRepairs(): Flow<List<Repair>>

    @Query("SELECT * FROM repairs WHERE issueId = :issueId ORDER BY createdAt DESC")
    fun getRepairsByIssue(issueId: Long): Flow<List<Repair>>

    @Query("SELECT * FROM repairs WHERE id = :id")
    fun getRepairById(id: Long): Flow<Repair?>

    @Query("SELECT * FROM repairs WHERE status != 'Done' ORDER BY scheduledDate ASC")
    fun getPendingRepairs(): Flow<List<Repair>>

    @Query("SELECT * FROM repairs WHERE scheduledDate IS NOT NULL AND status != 'Done' ORDER BY scheduledDate ASC")
    fun getScheduledRepairs(): Flow<List<Repair>>

    @Query("SELECT COUNT(*) FROM repairs WHERE status != 'Done'")
    fun getPendingRepairCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepair(repair: Repair): Long

    @Update
    suspend fun updateRepair(repair: Repair)

    @Delete
    suspend fun deleteRepair(repair: Repair)
}
