package com.housemo.monisto.data.local.dao

import androidx.room.*
import com.housemo.monisto.data.local.entity.Issue
import kotlinx.coroutines.flow.Flow

@Dao
interface IssueDao {
    @Query("SELECT * FROM issues ORDER BY createdAt DESC")
    fun getAllIssues(): Flow<List<Issue>>

    @Query("SELECT * FROM issues WHERE inspectionId = :inspectionId ORDER BY severity DESC")
    fun getIssuesByInspection(inspectionId: Long): Flow<List<Issue>>

    @Query("SELECT * FROM issues WHERE id = :id")
    fun getIssueById(id: Long): Flow<Issue?>

    @Query("SELECT COUNT(*) FROM issues WHERE isResolved = 0")
    fun getOpenIssueCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM issues WHERE isResolved = 0 AND severity = :severity")
    fun getOpenIssueCountBySeverity(severity: String): Flow<Int>

    @Query("SELECT * FROM issues WHERE isResolved = 0 ORDER BY createdAt DESC LIMIT 10")
    fun getOpenIssues(): Flow<List<Issue>>

    @Query("""
        SELECT issues.* FROM issues 
        INNER JOIN inspections ON issues.inspectionId = inspections.id 
        WHERE inspections.buildingId = :buildingId AND issues.isResolved = 0
    """)
    suspend fun getOpenIssuesByBuildingOnce(buildingId: Long): List<Issue>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssue(issue: Issue): Long

    @Update
    suspend fun updateIssue(issue: Issue)

    @Delete
    suspend fun deleteIssue(issue: Issue)
}
