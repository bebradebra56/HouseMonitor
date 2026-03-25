package com.housemo.monisto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val details: String,
    val entityType: String = "",
    val entityId: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)
