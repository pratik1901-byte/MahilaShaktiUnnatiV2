package com.example.mahilashaktiunnativ2.ui.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "savings_entries"
)

data class SavingsEntryEntity(

    @PrimaryKey(autoGenerate = true)

    val id: Int = 0,

    val memberId: Int,

    val weekNumber: Int,

    val amount: Int,

    val status: String,

    val paymentDate: Long,

    val remarks: String = ""
)