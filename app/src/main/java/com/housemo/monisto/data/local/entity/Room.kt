package com.housemo.monisto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rooms")
data class Room(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val floorId: Long,
    val name: String,
    val area: Float,
    val purpose: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
