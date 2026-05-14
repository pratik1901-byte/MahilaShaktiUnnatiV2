package com.example.mahilashaktiunnativ2.ui.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "members"
)

data class MemberEntity(

    @PrimaryKey(autoGenerate = true)

    val id: Int = 0,

    val name: String,

    val memberId: String,

    val age: Int,

    val dateOfBirth: String = "",

    val phoneNumber: String,

    val aadhaarNumber: String,

    val village: String,

    val address: String,

    val joiningDate: String,

    val weeklySavingAmount: Int,

    val savingsStatus: String,

    val savingsAmount: Int,

    val loansTaken: String,

    val nomineeName: String,

    val nomineePhoneNumber: String,

    val nomineeRelation: String,

    val photoUri: String?,

    val isArchived: Boolean = false,

    val archivedDate: Long? = null,

    val archiveReason: String = ""
)
