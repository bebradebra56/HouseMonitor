package com.housemo.monisto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "buildings")
data class Building(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val address: String,
    val floorsCount: Int,
    val conditionScore: Float = 100f,
    val yearBuilt: Int = 0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
