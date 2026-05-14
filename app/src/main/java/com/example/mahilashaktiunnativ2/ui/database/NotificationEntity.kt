package com.example.mahilashaktiunnativ2.ui.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val eventType: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val dateKey: String
)
