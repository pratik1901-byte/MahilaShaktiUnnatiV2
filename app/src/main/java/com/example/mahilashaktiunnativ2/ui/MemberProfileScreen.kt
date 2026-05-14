package com.example.mahilashaktiunnativ2.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mahilashaktiunnativ2.ui.database.*
import com.example.mahilashaktiunnativ2.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MemberProfileScreen(

    member: MemberEntity,

    onBack: () -> Unit

) {

    val viewModel: MemberViewModel =
        viewModel()

    var currentMember by remember(member.id) {

        mutableStateOf(member)
    }

    val transactions by
    viewModel
        .getTransactionsForMember(member.id)
        .collectAsState(initial = emptyList())

    val savingsEntries by
    viewModel
        .getSavingsEntriesForMember(member.id)
        .collectAsState(initial = emptyList())

    val loans by
    viewModel
        .getLoansForMember(member.memberId)
        .collectAsState(initial = emptyList())

    var currentWeek by remember {

        mutableIntStateOf(
            savingsEntries.size + 1
        )
    }

    var savingsAmount by remember {

        mutableIntStateOf(
            member.savingsAmount
        )
    }

    var showLoanDialog by remember {

        mutableStateOf(false)
    }

    var showLoanEligibilityDialog by remember {

        mutableStateOf(false)
    }

    var showSavingsDialog by remember {

        mutableStateOf(false)
    }

    var showEditDialog by remember {

        mutableStateOf(false)
    }

    var loanMessage by remember {

        mutableStateOf("")
    }

    val activeLoan =
        loans.firstOrNull {
            it.status == "Active"
        }

    val totalSavings by viewModel.totalSavings.collectAsState()
    val totalPendingLoans by viewModel.totalPendingLoans.collectAsState()
    val availableFund =
        (totalSavings ?: 0) - (totalPendingLoans ?: 0)
    val minimumMemberSavingsForLoan = 500
    val minimumShgFundForLoan = 5000
    val maximumLoanBySavings =
        savingsAmount * 3
    val maximumLoanAllowed =
        minOf(
            availableFund,
            maximumLoanBySavings
        )

    LaunchedEffect(savingsEntries.size) {

        currentWeek = savingsEntries.size + 1
    }

    if (loanMessage.isNotEmpty()) {

        AlertDialog(
            onDismissRequest = {
                loanMessage = ""
            },
            confirmButton = {
                Button(
                    onClick = {
                        loanMessage = ""
                    }
                ) {
                    Text("OK")
                }
            },
            title = {
                Text("Loan Notice")
            },
            text = {
                Text(loanMessage)
            }
        )
    }

    if (showEditDialog) {

        EditMemberDialog(
            member = currentMember,
            onDismiss = {
                showEditDialog = false
            },
            onSave = { updatedMember ->
                currentMember = updatedMember
                viewModel.updateMember(updatedMember)
                savingsAmount = updatedMember.savingsAmount
                showEditDialog = false
            }
        )
    }

    if (showLoanEligibilityDialog) {

        AlertDialog(
            onDismissRequest = {
                showLoanEligibilityDialog = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        val eligibilityMessage =
                            loanEligibilityMessage(
                                hasActiveLoan = activeLoan != null,
                                memberSavings = savingsAmount,
                                minimumMemberSavings = minimumMemberSavingsForLoan,
                                minimumShgFund = minimumShgFundForLoan,
                                availableFund = availableFund,
                                maxLoanAllowed = maximumLoanAllowed
                            )

                        if (eligibilityMessage == null) {
                            showLoanEligibilityDialog = false
                            showLoanDialog = true
                        } else {
                            showLoanEligibilityDialog = false
                            loanMessage = eligibilityMessage
                        }
                    }
                ) {
                    Text("Continue")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showLoanEligibilityDialog = false
                    }
                ) {
                    Text("Back")
                }
            },
            title = {
                Text("Loan Eligibility Criteria")
            },
            text = {
                Text(
                    "Before issuing a loan, the member must:\n\n" +
                            "- Have no active loan running.\n" +
                            "- Have minimum savings of Rs $minimumMemberSavingsForLoan.\n" +
                            "- SHG available fund should be at least Rs $minimumShgFundForLoan.\n" +
                            "- Request an amount within the available SHG fund.\n" +
                            "- Request an amount up to 3x her savings.\n" +
                            "- Maintain regular savings and repayment discipline.\n\n" +
                            "Available SHG fund: Rs $availableFund\n" +
                            "Member savings: Rs $savingsAmount\n" +
                            "Maximum loan allowed now: Rs ${maximumLoanAllowed.coerceAtLeast(0)}"
                )
            }
        )
    }

    if (showLoanDialog) {

        var loanAmount by remember {

            mutableStateOf("")
        }

        var interestRate by remember {

            mutableStateOf("")
        }

        var selectedDuration by remember {

            mutableStateOf("3 Months")
        }

        var loanPurpose by remember {

            mutableStateOf("")
        }

        val durationMonths = when (selectedDuration) {

            "3 Months" -> 3

            "6 Months" -> 6

            "12 Months" -> 12

            else -> 3
        }

        AlertDialog(

            onDismissRequest = {

                showLoanDialog = false
            },

            confirmButton = {

                Button(

                    onClick = {

                        val requestedAmount =
                            loanAmount.toIntOrNull() ?: 0

                        val eligibilityMessage =
                            loanEligibilityMessage(
                                hasActiveLoan = activeLoan != null,
                                memberSavings = savingsAmount,
                                minimumMemberSavings = minimumMemberSavingsForLoan,
                                minimumShgFund = minimumShgFundForLoan,
                                availableFund = availableFund,
                                maxLoanAllowed = maximumLoanAllowed,
                                requestedAmount = requestedAmount
                            )

                        if (eligibilityMessage != null) {
                            loanMessage = eligibilityMessage
                            showLoanDialog = false
                        } else {
                            val dueDate =

                                System.currentTimeMillis() +

                                        (
                                                durationMonths *
                                                        30L *
                                                        24L *
                                                        60L *
                                                        60L *
                                                        1000L
                                                )

                            viewModel.issueLoan(

                                memberId = member.memberId,

                                loanAmount =
                                    requestedAmount,

                                interestRate =
                                    interestRate.toFloatOrNull()
                                        ?: 0f,

                                dueDate = dueDate,

                                remarks = loanPurpose,

                                onResult = { message ->

                                    loanMessage = message
                                }
                            )

                            showLoanDialog = false
                        }
                    }

                ) {

                    Text("Issue Loan")
                }
            },

            dismissButton = {

                OutlinedButton(

                    onClick = {

                        showLoanDialog = false
                    }

                ) {

                    Text("Cancel")
                }
            },

            title = {

                Text("Issue Loan")
            },

            text = {

                Column {

                    OutlinedTextField(

                        value = loanAmount,

                        onValueChange = {

                            loanAmount = it
                        },

                        label = {

                            Text("Loan Amount")
                        }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    OutlinedTextField(

                        value = interestRate,

                        onValueChange = {

                            interestRate = it
                        },

                        label = {

                            Text("Interest Rate %")
                        }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    Text(

                        text = "Loan Duration",

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Row(

                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)

                    ) {

                        FilterChip(

                            selected =
                                selectedDuration ==
                                        "3 Months",

                            onClick = {

                                selectedDuration =
                                    "3 Months"
                            },

                            label = {

                                Text("3 Months")
                            }
                        )

                        FilterChip(

                            selected =
                                selectedDuration ==
                                        "6 Months",

                            onClick = {

                                selectedDuration =
                                    "6 Months"
                            },

                            label = {

                                Text("6 Months")
                            }
                        )

                        FilterChip(

                            selected =
                                selectedDuration ==
                                        "12 Months",

                            onClick = {

                                selectedDuration =
                                    "12 Months"
                            },

                            label = {

                                Text("12 Months")
                            }
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    OutlinedTextField(

                        value = loanPurpose,

                        onValueChange = {

                            loanPurpose = it
                        },

                        label = {

                            Text("Loan Purpose")
                        },

                        placeholder = {

                            Text(
                                "Medical / Education / Business"
                            )
                        }
                    )
                }
            }
        )
    }

    if (showSavingsDialog) {

        var customAmount by remember {

            mutableStateOf(
                currentMember.weeklySavingAmount.toString()
            )
        }

        var remarks by remember {

            mutableStateOf("")
        }

        var selectedStatus by remember {

            mutableStateOf("Paid")
        }

        AlertDialog(

            onDismissRequest = {

                showSavingsDialog = false
            },

            confirmButton = {

                Button(

                    onClick = {

                        val enteredAmount =
                            customAmount.toIntOrNull()
                                ?: 0

                        viewModel.addWeeklySavingsEntry(

                            memberId = member.id,

                            weekNumber = currentWeek,

                            amount = enteredAmount,

                            status = selectedStatus,

                            remarks = remarks
                        )

                        if (selectedStatus == "Paid") {

                            viewModel.addSavingsTransaction(

                                member = member,

                                currentSavings =
                                    savingsAmount,

                                amount = enteredAmount,

                                note =
                                    "Week $currentWeek Savings"
                            )

                            savingsAmount += enteredAmount
                        }

                        currentWeek++

                        showSavingsDialog = false
                    }

                ) {

                    Text("Save Entry")
                }
            },

            dismissButton = {

                OutlinedButton(

                    onClick = {

                        showSavingsDialog = false
                    }

                ) {

                    Text("Cancel")
                }
            },

            title = {

                Text("Weekly Savings Entry")
            },

            text = {

                Column {

                    OutlinedTextField(

                        value = customAmount,

                        onValueChange = {

                            customAmount = it
                        },

                        label = {

                            Text("Contribution Amount")
                        }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(
                        text = "Payment Status",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected =
                                selectedStatus == "Paid",
                            onClick = {
                                selectedStatus = "Paid"
                            },
                            label = {
                                Text("Paid")
                            }
                        )

                        FilterChip(
                            selected =
                                selectedStatus == "Pending",
                            onClick = {
                                selectedStatus = "Pending"
                            },
                            label = {
                                Text("Pending")
                            }
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    OutlinedTextField(

                        value = remarks,

                        onValueChange = {

                            remarks = it
                        },

                        label = {

                            Text("Remarks")
                        }
                    )
                }
            }
        )
    }

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(20.dp)

    ) {

        item {

            Row(

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                IconButton(
                    onClick = onBack
                ) {

                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Text(

                    text = currentMember.name,

                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall
                )
            }

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(28.dp),
                        clip = false
                    )
                    .animateContentSize(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                )
            ) {

                Column(

                    horizontalAlignment =
                        Alignment.CenterHorizontally,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(22.dp)

                ) {

                if (currentMember.photoUri != null) {

                    AsyncImage(

                        model = currentMember.photoUri,

                        contentDescription = null,

                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),

                        contentScale =
                            ContentScale.Crop
                    )

                } else {

                    Box(

                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(LightGreen),

                        contentAlignment =
                            Alignment.Center

                    ) {

                        Icon(

                            Icons.Default.Person,

                            contentDescription = null
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Text(

                    text =
                        "Member ID: ${currentMember.memberId}",

                    color = SecondaryText
                )

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileStatCard(
                        title = "Savings",
                        value = "Rs $savingsAmount",
                        modifier = Modifier.weight(1f)
                    )

                    ProfileStatCard(
                        title = "Weekly",
                        value = "Rs ${currentMember.weeklySavingAmount}",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileStatCard(
                        title = "Loans",
                        value = if (activeLoan != null) {
                            "Active"
                        } else {
                            "None"
                        },
                        modifier = Modifier.weight(1f)
                    )

                    ProfileStatCard(
                        title = "Village",
                        value = currentMember.village,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(

                    text = "Member Details",

                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall
                )

                TextButton(
                    onClick = {
                        showEditDialog = true
                    }
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text("Edit")
                }
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val displayedAge =
                        if (currentMember.dateOfBirth.isNotBlank()) {
                            calculateAgeFromDate(currentMember.dateOfBirth)
                        } else {
                            currentMember.age
                        }

                    ProfileDetailRow("Date of Birth", currentMember.dateOfBirth.ifBlank { "Not selected" })
                    ProfileDetailRow("Age", "$displayedAge years")
                    ProfileDetailRow("Phone", currentMember.phoneNumber)
                    ProfileDetailRow("Aadhaar", currentMember.aadhaarNumber)
                    ProfileDetailRow("Village", currentMember.village)
                    ProfileDetailRow("Address", currentMember.address.ifBlank { "Not provided" })
                    ProfileDetailRow("Joining Date", currentMember.joiningDate.ifBlank { "Not selected" })
                    ProfileDetailRow("Nominee", currentMember.nomineeName.ifBlank { "Not provided" })
                    ProfileDetailRow("Nominee Phone", currentMember.nomineePhoneNumber.ifBlank { "Not provided" })
                    ProfileDetailRow("Relation", currentMember.nomineeRelation.ifBlank { "Not provided" })
                }
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            Text(

                text = "Loan Management",

                style =
                    MaterialTheme
                        .typography
                        .headlineSmall
            )

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Button(

                    onClick = {

                        showSavingsDialog = true
                    },

                    modifier =
                        Modifier.weight(1f)

                ) {

                    Text("Savings")
                }

                Button(

                    onClick = {

                        if (activeLoan != null) {
                            loanMessage =
                                "We cannot issue two or more loans at a time. Please complete the existing active loan first, then apply for a new loan."
                        } else {
                            showLoanEligibilityDialog = true
                        }
                    },

                    modifier =
                        Modifier.weight(1f)

                ) {

                    Text("Issue Loan")
                }
            }

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Text(

                text = "Weekly Savings History",

                style =
                    MaterialTheme
                        .typography
                        .headlineSmall
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            if (savingsEntries.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    )
                ) {
                    Text(
                        text = "No weekly savings entries yet.",
                        color = SecondaryText,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        items(savingsEntries) { entry ->

            SavingsEntryCard(
                entry = entry,
                onMarkPaid = {
                    viewModel.updateSavingsEntry(
                        entry.copy(
                            status = "Paid",
                            paymentDate = System.currentTimeMillis(),
                            remarks = if (entry.remarks.isBlank()) {
                                "Marked paid after pending"
                            } else {
                                "${entry.remarks} - Marked paid"
                            }
                        )
                    )

                    viewModel.addSavingsTransaction(
                        member = currentMember,
                        currentSavings = savingsAmount,
                        amount = entry.amount,
                        note = "Week ${entry.weekNumber} Pending Cleared"
                    )

                    savingsAmount += entry.amount
                }
            )
        }

        item {

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Text(

                text = "Loan History",

                style =
                    MaterialTheme
                        .typography
                        .headlineSmall
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )
        }

        items(loans) { loan ->

            LoanCard(loan = loan)
        }
    }
}

@Composable
fun SavingsEntryCard(
    entry: SavingsEntryEntity,
    onMarkPaid: () -> Unit
) {
    val formattedDate =
        remember(entry.paymentDate) {
            SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
            ).format(Date(entry.paymentDate))
        }

    val statusColor =
        if (entry.status == "Paid") SuccessGreen else WarningOrange

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Week ${entry.weekNumber}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formattedDate,
                    color = SecondaryText,
                    style = MaterialTheme.typography.bodySmall
                )

                if (entry.remarks.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.remarks,
                        color = SecondaryText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Rs ${entry.amount}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = entry.status,
                        color = statusColor,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (entry.status == "Pending") {
                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = onMarkPaid
                    ) {
                        Text("Mark Paid")
                    }
                }
            }
        }
    }
}

@Composable
fun EditMemberDialog(

    member: MemberEntity,

    onDismiss: () -> Unit,

    onSave: (MemberEntity) -> Unit

) {

    var name by remember { mutableStateOf(member.name) }
    var dateOfBirth by remember { mutableStateOf(member.dateOfBirth) }
    var phoneNumber by remember { mutableStateOf(member.phoneNumber) }
    var village by remember { mutableStateOf(member.village) }
    var address by remember { mutableStateOf(member.address) }
    var weeklySavingAmount by remember { mutableStateOf(member.weeklySavingAmount.toString()) }
    var nomineeName by remember { mutableStateOf(member.nomineeName) }
    var nomineePhoneNumber by remember { mutableStateOf(member.nomineePhoneNumber) }
    var nomineeRelation by remember { mutableStateOf(member.nomineeRelation) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val calculatedAge =
                        calculateAgeFromDate(dateOfBirth)

                    val validPhone =
                        phoneNumber.length == 10 &&
                                phoneNumber.all { it.isDigit() }

                    val validNomineePhone =
                        nomineePhoneNumber.length == 10 &&
                                nomineePhoneNumber.all { it.isDigit() }

                    if (name.isBlank() || village.isBlank() || dateOfBirth.isBlank()) {
                        errorMessage = "Name, DOB and village are required"
                    } else if (calculatedAge !in 18..80) {
                        errorMessage = "Member age must be between 18 and 80"
                    } else if (!validPhone) {
                        errorMessage = "Enter valid 10-digit phone number"
                    } else if (!validNomineePhone) {
                        errorMessage = "Enter valid nominee phone number"
                    } else {
                        onSave(
                            member.copy(
                                name = name,
                                age = calculatedAge,
                                dateOfBirth = dateOfBirth,
                                phoneNumber = phoneNumber,
                                village = village,
                                address = address,
                                weeklySavingAmount = weeklySavingAmount.toIntOrNull()
                                    ?: member.weeklySavingAmount,
                                nomineeName = nomineeName,
                                nomineePhoneNumber = nomineePhoneNumber,
                                nomineeRelation = nomineeRelation
                            )
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        },
        title = {
            Text("Edit Member Details")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                MemberInputField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = "Full Name"
                )

                MemberDateInputField(
                    value = dateOfBirth,
                    onDateSelected = {
                        dateOfBirth = it
                    },
                    label = "Date of Birth"
                )

                MemberInputField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                    },
                    label = "Phone Number",
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                )

                MemberInputField(
                    value = village,
                    onValueChange = {
                        village = it
                    },
                    label = "Village"
                )

                MemberInputField(
                    value = address,
                    onValueChange = {
                        address = it
                    },
                    label = "Address"
                )

                MemberInputField(
                    value = weeklySavingAmount,
                    onValueChange = {
                        weeklySavingAmount = it
                    },
                    label = "Weekly Savings Amount",
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )

                MemberInputField(
                    value = nomineeName,
                    onValueChange = {
                        nomineeName = it
                    },
                    label = "Nominee Name"
                )

                MemberInputField(
                    value = nomineePhoneNumber,
                    onValueChange = {
                        nomineePhoneNumber = it
                    },
                    label = "Nominee Phone Number",
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                )

                MemberInputField(
                    value = nomineeRelation,
                    onValueChange = {
                        nomineeRelation = it
                    },
                    label = "Nominee Relation"
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}

@Composable
fun ProfileStatCard(

    title: String,

    value: String,

    modifier: Modifier = Modifier

) {

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = LightGreen
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Text(
                text = title,
                color = SecondaryText,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ProfileDetailRow(

    label: String,

    value: String

) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            color = SecondaryText,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1.3f)
        )
    }
}

fun loanEligibilityMessage(
    hasActiveLoan: Boolean,
    memberSavings: Int,
    minimumMemberSavings: Int,
    minimumShgFund: Int,
    availableFund: Int,
    maxLoanAllowed: Int,
    requestedAmount: Int? = null
): String? {
    return when {
        hasActiveLoan ->
            "Not eligible for loan: this member already has an active loan. Complete the older loan first, then apply again."

        memberSavings < minimumMemberSavings ->
            "Not eligible for loan: minimum member savings required is Rs $minimumMemberSavings. Current savings is Rs $memberSavings."

        availableFund <= 0 ->
            "Not eligible for loan: SHG available fund is Rs $availableFund. Add more savings or recover pending loans first."

        availableFund < minimumShgFund ->
            "Not eligible for loan: SHG available fund should be at least Rs $minimumShgFund before issuing loans. Current available fund is Rs $availableFund."

        maxLoanAllowed <= 0 ->
            "Not eligible for loan: maximum eligible loan amount is Rs 0 based on current savings and available fund."

        requestedAmount != null && requestedAmount <= 0 ->
            "Not eligible for loan: enter a valid loan amount."

        requestedAmount != null && requestedAmount > availableFund ->
            "Not eligible for loan: requested amount Rs $requestedAmount is more than the available SHG fund Rs $availableFund."

        requestedAmount != null && requestedAmount > maxLoanAllowed ->
            "Not eligible for loan: maximum loan allowed for this member is Rs $maxLoanAllowed based on savings and available fund."

        else -> null
    }
}

@Composable
fun LoanCard(

    loan: LoanEntity,

    viewModel: MemberViewModel =
        viewModel()

) {

    var showRepaymentDialog by remember {

        mutableStateOf(false)
    }

    val currentTime =
        System.currentTimeMillis()

    val isOverdue =
        currentTime > loan.dueDate &&
                loan.status == "Active"

    val repaymentProgress =
        if (loan.loanAmount > 0)

            loan.paidAmount.toFloat() /
                    loan.loanAmount.toFloat()

        else

            0f

    val animatedRepaymentProgress by animateFloatAsState(
        targetValue = repaymentProgress.coerceIn(0f, 1f),
        animationSpec = tween(650),
        label = "loan_repayment_progress"
    )

    val daysRemaining =

        ((loan.dueDate - currentTime)
                / (1000 * 60 * 60 * 24))
            .toInt()

    val issueDateFormatted =
        SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(Date(loan.issueDate))

    val dueDateFormatted =
        SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(Date(loan.dueDate))

    if (showRepaymentDialog) {

        var repaymentAmount by remember {

            mutableStateOf("")
        }

        var repaymentRemarks by remember {

            mutableStateOf("")
        }

        AlertDialog(

            onDismissRequest = {

                showRepaymentDialog = false
            },

            confirmButton = {

                Button(

                    onClick = {

                        val amount =
                            repaymentAmount.toIntOrNull()
                                ?: 0

                        if (amount > 0) {

                            viewModel.updateLoanPayment(

                                loan = loan,

                                paymentAmount = amount
                            )
                        }

                        showRepaymentDialog = false
                    }

                ) {

                    Text("Confirm Payment")
                }
            },

            dismissButton = {

                OutlinedButton(

                    onClick = {

                        showRepaymentDialog = false
                    }

                ) {

                    Text("Cancel")
                }
            },

            title = {

                Text("Loan Repayment")
            },

            text = {

                Column {

                    OutlinedTextField(

                        value = repaymentAmount,

                        onValueChange = {

                            repaymentAmount = it
                        },

                        label = {

                            Text("Repayment Amount")
                        }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    OutlinedTextField(

                        value = repaymentRemarks,

                        onValueChange = {

                            repaymentRemarks = it
                        },

                        label = {

                            Text("Remarks")
                        }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )

                    Text(

                        text =
                            "Remaining Loan: Rs ${loan.remainingAmount}",

                        color = SecondaryText
                    )
                }
            }
        )
    }

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(22.dp),
                clip = false
            )
            .animateContentSize(),

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    CardBackground
            )

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)

        ) {

            Row(

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(

                    text =
                        "Loan Amount: Rs ${loan.loanAmount}",

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )

                Surface(

                    shape =
                        RoundedCornerShape(12.dp),

                    color = if (isOverdue)

                        MaterialTheme.colorScheme.error

                    else if (
                        loan.status == "Completed"
                    )

                        SuccessGreen

                    else

                        PrimaryGreen

                ) {

                    Text(

                        text = if (isOverdue)

                            "Overdue"

                        else

                            loan.status,

                        modifier =
                            Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),

                        color = CardBackground
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(
                text =
                    "Paid: Rs ${loan.paidAmount}"
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Remaining: Rs ${loan.remainingAmount}"
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Interest Rate: ${loan.interestRate}%"
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Purpose: ${loan.remarks}"
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Issued On: $issueDateFormatted"
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Due Date: $dueDateFormatted"
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(

                text = if (daysRemaining >= 0)

                    "$daysRemaining days remaining"

                else

                    "${kotlin.math.abs(daysRemaining)} days overdue",

                color = if (isOverdue)

                    MaterialTheme.colorScheme.error

                else

                    SecondaryText
            )

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            LinearProgressIndicator(

                progress = {

                    animatedRepaymentProgress
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(10.dp),

                trackColor =
                    LightGreen
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(

                text =
                    "${(animatedRepaymentProgress * 100).toInt()}% Repaid",

                color = SecondaryText
            )

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            if (loan.status == "Active") {

                Button(

                    onClick = {

                        showRepaymentDialog = true
                    },

                    modifier =
                        Modifier.fillMaxWidth()

                ) {

                    Text("Make Repayment")
                }
            }
        }
    }
}

