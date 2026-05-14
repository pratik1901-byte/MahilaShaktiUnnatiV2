package com.example.mahilashaktiunnativ2.ui.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsEntryDao {

    @Insert(
        onConflict =
            OnConflictStrategy.REPLACE
    )

    suspend fun insertEntry(
        entry: SavingsEntryEntity
    )

    @Update

    suspend fun updateEntry(
        entry: SavingsEntryEntity
    )

    @Query(
        "SELECT * FROM savings_entries WHERE memberId = :memberId ORDER BY weekNumber DESC"
    )

    fun getEntriesForMember(
        memberId: Int
    ): Flow<List<SavingsEntryEntity>>

    @Query(
        "SELECT SUM(amount) FROM savings_entries WHERE status = 'Paid'"
    )

    fun getTotalSavings():
            Flow<Int?>

    @Query(
        "SELECT COUNT(*) FROM savings_entries WHERE status = 'Pending'"
    )

    fun getPendingCount():
            Flow<Int>
}