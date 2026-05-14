package com.example.mahilashaktiunnativ2.ui.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "loans"
)

data class LoanEntity(

    @PrimaryKey(autoGenerate = true)

    val id: Int = 0,

    val memberId: String,

    val loanAmount: Int,

    val paidAmount: Int,

    val remainingAmount: Int,

    val interestRate: Float,

    val issueDate: Long,

    val dueDate: Long,

    val status: String,

    val remarks: String = ""
)