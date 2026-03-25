package com.housemo.monisto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val issueId: Long,
    val uri: String,
    val location: String,
    val caption: String = "",
    val capturedAt: Long = System.currentTimeMillis()
)
