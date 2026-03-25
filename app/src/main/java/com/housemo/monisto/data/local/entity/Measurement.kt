package com.housemo.monisto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val issueId: Long,
    val type: String,
    val value: Float,
    val unit: String,
    val notes: String = "",
    val measuredAt: Long = System.currentTimeMillis()
)
