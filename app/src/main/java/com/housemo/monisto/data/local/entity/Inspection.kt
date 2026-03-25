package com.housemo.monisto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inspections")
data class Inspection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val buildingId: Long,
    val date: Long,
    val inspector: String,
    val notes: String,
    val overallCondition: String = "Good",
    val createdAt: Long = System.currentTimeMillis()
)
