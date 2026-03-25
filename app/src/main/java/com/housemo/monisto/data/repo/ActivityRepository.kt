package com.housemo.monisto.data.repo

import com.housemo.monisto.data.local.dao.ActivityLogDao
import com.housemo.monisto.data.local.entity.ActivityLog
import kotlinx.coroutines.flow.Flow

class ActivityRepository(
    private val activityLogDao: ActivityLogDao
) {
    fun getAllLogs(): Flow<List<ActivityLog>> = activityLogDao.getAllLogs()
    fun getRecentLogs(limit: Int = 20): Flow<List<ActivityLog>> = activityLogDao.getRecentLogs(limit)
    suspend fun logAction(action: String, details: String, entityType: String = "", entityId: Long = 0) {
        activityLogDao.insertLog(ActivityLog(
            action = action,
            details = details,
            entityType = entityType,
            entityId = entityId
        ))
    }
}
