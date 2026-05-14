package com.example.mahilashaktiunnativ2.ui.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao

interface SavingsTransactionDao {

    @Insert(
        onConflict =
            OnConflictStrategy.REPLACE
    )

    suspend fun insertTransaction(
        transaction:
        SavingsTransactionEntity
    )

    @Query(
        "SELECT * FROM transactions WHERE memberId = :memberId ORDER BY timestamp DESC"
    )

    fun getTransactionsForMember(
        memberId: Int
    ): Flow<List<SavingsTransactionEntity>>
}