package com.housemo.monisto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "structures")
data class Structure(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomId: Long,
    val type: String,
    val material: String,
    val condition: String,
    val notes: String = "",
    val lastInspectedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
