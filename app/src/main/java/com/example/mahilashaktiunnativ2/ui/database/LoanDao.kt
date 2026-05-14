package com.example.mahilashaktiunnativ2.ui.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {

    @Insert(
        onConflict =
            OnConflictStrategy.REPLACE
    )

    suspend fun insertLoan(
        loan: LoanEntity
    )

    @Update

    suspend fun updateLoan(
        loan: LoanEntity
    )

    @Query(
        "SELECT * FROM loans WHERE memberId = :memberId ORDER BY issueDate DESC"
    )

    fun getLoansForMember(
        memberId: String
    ): Flow<List<LoanEntity>>

    @Query(
        "SELECT * FROM loans ORDER BY issueDate DESC"
    )

    fun getAllLoans():
            Flow<List<LoanEntity>>

    @Query(
        "SELECT * FROM loans WHERE memberId = :memberId AND status = 'Active' LIMIT 1"
    )

    suspend fun getActiveLoanForMember(
        memberId: String
    ): LoanEntity?

    @Query(
        "SELECT SUM(remainingAmount) FROM loans WHERE status = 'Active'"
    )

    fun getTotalPendingLoans():
            Flow<Int?>

    @Query(
        "SELECT COUNT(*) FROM loans WHERE status = 'Active'"
    )

    fun getActiveLoanCount():
            Flow<Int>

    @Query(
        "SELECT SUM(loanAmount) FROM loans"
    )

    fun getTotalLoanAmount():
            Flow<Int?>

    @Query(
        "SELECT SUM(paidAmount) FROM loans"
    )

    fun getTotalLoanPaidAmount():
            Flow<Int?>
}
