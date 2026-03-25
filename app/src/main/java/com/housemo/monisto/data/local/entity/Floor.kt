package com.housemo.monisto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "floors")
data class Floor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val buildingId: Long,
    val name: String,
    val level: Int,
    val createdAt: Long = System.currentTimeMillis()
)
