package com.housemo.monisto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materials")
data class Material(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val description: String = "",
    val quantity: Float = 0f,
    val unit: String = "pcs",
    val supplier: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
