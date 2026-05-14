package com.example.mahilashaktiunnativ2.ui.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MemberViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database =
        AppDatabase.getDatabase(application)

    private val repository =
        MemberRepository(
            database.memberDao()
        )

    private val transactionDao =
        database.transactionDao()

    private val savingsEntryDao =
        database.savingsEntryDao()

    private val loanDao =
        database.loanDao()

    private val notificationDao =
        database.notificationDao()

    val members =

        database
            .memberDao()
            .getAllMembers()
            .stateIn(

                scope = viewModelScope,

                started =
                    SharingStarted.WhileSubscribed(5000),

                initialValue = emptyList()
            )

    val totalSavings =

        savingsEntryDao
            .getTotalSavings()
            .stateIn(

                scope = viewModelScope,

                started =
                    SharingStarted.WhileSubscribed(5000),

                initialValue = 0
            )

    val pendingSavingsCount =

        savingsEntryDao
            .getPendingCount()
            .stateIn(

                scope = viewModelScope,

                started =
                    SharingStarted.WhileSubscribed(5000),

                initialValue = 0
            )

    val activeLoanCount =

        loanDao
            .getActiveLoanCount()
            .stateIn(

                scope = viewModelScope,

                started =
                    SharingStarted.WhileSubscribed(5000),

                initialValue = 0
            )

    val allLoans =

        loanDao
            .getAllLoans()
            .stateIn(

                scope = viewModelScope,

                started =
                    SharingStarted.WhileSubscribed(5000),

                initialValue = emptyList()
            )

    val archivedMembers =

        repository
            .archivedMembers
            .stateIn(

                scope = viewModelScope,

                started =
                    SharingStarted.WhileSubscribed(5000),

                initialValue = emptyList()
            )

    val notifications =

        notificationDao
            .getAllNotifications()
            .stateIn(

                scope = viewModelScope,

                started =
                    SharingStarted.WhileSubscribed(5000),

                initialValue = emptyList()
            )

    val totalPendingLoans =

        loanDao
            .getTotalPendingLoans()
            .stateIn(

                scope = viewModelScope,

                started =
                    SharingStarted.WhileSubscribed(5000),

                initialValue = 0
            )

    val totalLoanAmount =

        loanDao
            .getTotalLoanAmount()
            .stateIn(

                scope = viewModelScope,

                started =
                    SharingStarted.WhileSubscribed(5000),

                initialValue = 0
            )

    val totalLoanPaidAmount =

        loanDao
            .getTotalLoanPaidAmount()
            .stateIn(

                scope = viewModelScope,

                started =
                    SharingStarted.WhileSubscribed(5000),

                initialValue = 0
            )

    fun addMember(
        member: MemberEntity
    ) {

        viewModelScope.launch {

            repository.insertMember(member)

            addNotification(
                eventType = "Member",
                title = "New member registered",
                message = "${member.name} joined the SHG."
            )
        }
    }

    fun updateMember(
        member: MemberEntity
    ) {

        viewModelScope.launch {

            repository.updateMember(member)

            addNotification(
                eventType = "Member",
                title = "Member details updated",
                message = "${member.name}'s profile information was updated."
            )
        }
    }

    fun archiveMember(
        member: MemberEntity,
        reason: String = "Moved to past members"
    ) {

        viewModelScope.launch {

            repository.archiveMember(
                id = member.id,
                archivedDate = System.currentTimeMillis(),
                reason = reason
            )

            addNotification(
                eventType = "Member",
                title = "Member archived",
                message = "${member.name} was moved to past members."
            )
        }
    }

    fun restoreMember(
        member: MemberEntity
    ) {

        viewModelScope.launch {

            repository.restoreMember(member.id)

            addNotification(
                eventType = "Member",
                title = "Member restored",
                message = "${member.name} was restored to active members."
            )
        }
    }

    fun addSavingsTransaction(

        member: MemberEntity,

        currentSavings: Int,

        amount: Int,

        note: String = "Savings Deposit"

    ) {

        viewModelScope.launch {

            val updatedMember =
                member.copy(

                    savingsAmount =
                        currentSavings + amount
                )

            repository.updateMember(
                updatedMember
            )

            transactionDao.insertTransaction(

                SavingsTransactionEntity(

                    memberId = member.id,

                    amount = amount,

                    type = "Deposit",

                    note = note,

                    timestamp =
                        System.currentTimeMillis()
                )
            )

            addNotification(
                eventType = "Savings",
                title = "Savings added",
                message = "${member.name} added savings of Rs $amount."
            )
        }
    }

    fun getTransactionsForMember(
        memberId: Int
    ) =

        transactionDao
            .getTransactionsForMember(
                memberId
            )

    fun addWeeklySavingsEntry(

        memberId: Int,

        weekNumber: Int,

        amount: Int,

        status: String,

        remarks: String = ""

    ) {

        viewModelScope.launch {

            savingsEntryDao.insertEntry(

                SavingsEntryEntity(

                    memberId = memberId,

                    weekNumber = weekNumber,

                    amount = amount,

                    status = status,

                    paymentDate =
                        System.currentTimeMillis(),

                    remarks = remarks
                )
            )

            addNotification(
                eventType = "Savings",
                title = "Weekly savings recorded",
                message = "Week $weekNumber savings marked as $status for member record #$memberId."
            )
        }
    }

    fun updateSavingsEntry(

        entry: SavingsEntryEntity

    ) {

        viewModelScope.launch {

            savingsEntryDao.updateEntry(
                entry
            )
        }
    }

    fun getSavingsEntriesForMember(
        memberId: Int
    ) =

        savingsEntryDao
            .getEntriesForMember(
                memberId
            )

    fun issueLoan(

        memberId: String,

        loanAmount: Int,

        interestRate: Float,

        dueDate: Long,

        remarks: String = "",

        onResult: (String) -> Unit = {}

    ) {

        viewModelScope.launch {

            val activeLoan =

                loanDao
                    .getActiveLoanForMember(
                        memberId
                    )

            if (activeLoan != null) {

                onResult(
                    "Cannot issue another loan. Complete the existing active loan first, then apply again."
                )

                return@launch
            }

            loanDao.insertLoan(

                LoanEntity(

                    memberId = memberId,

                    loanAmount = loanAmount,

                    paidAmount = 0,

                    remainingAmount =
                        loanAmount,

                    interestRate =
                        interestRate,

                    issueDate =
                        System.currentTimeMillis(),

                    dueDate = dueDate,

                    status = "Active",

                    remarks = remarks
                )
            )

            addNotification(
                eventType = "Loan",
                title = "Loan issued",
                message = "Loan of Rs $loanAmount issued for member $memberId."
            )

            onResult(
                "Loan issued successfully"
            )
        }
    }

    fun updateLoanPayment(

        loan: LoanEntity,

        paymentAmount: Int

    ) {

        viewModelScope.launch {

            val newPaidAmount =
                loan.paidAmount +
                        paymentAmount

            val newRemaining =
                loan.remainingAmount -
                        paymentAmount

            val updatedLoan =
                loan.copy(

                    paidAmount =
                        newPaidAmount,

                    remainingAmount =
                        newRemaining,

                    status = if (
                        newRemaining <= 0
                    )

                        "Completed"

                    else

                        "Active"
                )

            loanDao.updateLoan(
                updatedLoan
            )

            addNotification(
                eventType = "Repayment",
                title = "Loan repayment recorded",
                message = "Repayment of Rs $paymentAmount recorded for member ${loan.memberId}."
            )
        }
    }

    fun getLoansForMember(
        memberId: String
    ) =

        loanDao.getLoansForMember(
            memberId
        )

    private suspend fun addNotification(
        eventType: String,
        title: String,
        message: String
    ) {

        val now =
            System.currentTimeMillis()

        val dateKey =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date(now))

        notificationDao.insertNotification(
            NotificationEntity(
                eventType = eventType,
                title = title,
                message = message,
                timestamp = now,
                dateKey = dateKey
            )
        )
    }
}
