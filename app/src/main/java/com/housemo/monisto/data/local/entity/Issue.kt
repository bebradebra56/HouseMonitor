package com.housemo.monisto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "issues")
data class Issue(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val inspectionId: Long,
    val type: String,
    val location: String,
    val severity: String,
    val description: String,
    val isResolved: Boolean = false,
    val resolvedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
