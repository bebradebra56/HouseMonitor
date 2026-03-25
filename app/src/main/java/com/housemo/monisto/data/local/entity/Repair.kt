package com.housemo.monisto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repairs")
data class Repair(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val issueId: Long,
    val type: String,
    val description: String = "",
    val cost: Double = 0.0,
    val status: String = "Pending",
    val priority: String = "Medium",
    val scheduledDate: Long? = null,
    val completedDate: Long? = null,
    val contractor: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
