package com.example.mahilashaktiunnativ2.ui.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")

data class SavingsTransactionEntity(

    @PrimaryKey(autoGenerate = true)

    val id: Int = 0,

    val memberId: Int,

    val amount: Int,

    val type: String,

    val note: String,

    val timestamp: Long
)