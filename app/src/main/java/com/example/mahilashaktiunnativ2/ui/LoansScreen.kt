package com.example.mahilashaktiunnativ2.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mahilashaktiunnativ2.ui.database.LoanEntity
import com.example.mahilashaktiunnativ2.ui.database.MemberViewModel
import com.example.mahilashaktiunnativ2.ui.theme.AppBackground
import com.example.mahilashaktiunnativ2.ui.theme.CardBackground
import com.example.mahilashaktiunnativ2.ui.theme.LightGreen
import com.example.mahilashaktiunnativ2.ui.theme.PrimaryGreen
import com.example.mahilashaktiunnativ2.ui.theme.SecondaryText
import com.example.mahilashaktiunnativ2.ui.theme.SuccessGreen
import com.example.mahilashaktiunnativ2.ui.theme.WarningOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun LoansScreen() {

    val viewModel: MemberViewModel = viewModel()
    val loans by viewModel.allLoans.collectAsState()
    val members by viewModel.members.collectAsState()
    val totalPendingLoans by viewModel.totalPendingLoans.collectAsState()

    var searchText by remember {
        mutableStateOf("")
    }

    val memberNames =
        members.associate {
            it.memberId to it.name
        }

    val filteredLoans =
        loans.filter { loan ->
            val memberName =
                memberNames[loan.memberId] ?: loan.memberId

            memberName.contains(searchText, ignoreCase = true) ||
                    loan.memberId.contains(searchText, ignoreCase = true) ||
                    loan.status.contains(searchText, ignoreCase = true)
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Loan Tracker",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Live SHG loans, repayments and due dates.",
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LoanSummaryCard(
                    title = "Total Loans",
                    value = loans.size.toString(),
                    modifier = Modifier.weight(1f)
                )

                LoanSummaryCard(
                    title = "Pending Amount",
                    value = "₹${totalPendingLoans ?: 0}",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Search loans")
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(18.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (filteredLoans.isEmpty()) {
                Text(
                    text = "No loans issued yet.",
                    color = SecondaryText
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = filteredLoans,
                        key = { it.id }
                    ) { loan ->
                        LoanTrackerCard(
                            loan = loan,
                            memberName = memberNames[loan.memberId] ?: loan.memberId
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoanTrackerCard(
    loan: LoanEntity,
    memberName: String
) {
    val progress =
        if (loan.loanAmount > 0) {
            loan.paidAmount.toFloat() / loan.loanAmount.toFloat()
        } else {
            0f
        }

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(650),
        label = "loan_tracker_progress"
    )

    val dateFormat =
        remember {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        }

    val daysRemaining =
        ((loan.dueDate - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()

    val isOverdue =
        daysRemaining < 0 && loan.status == "Active"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            )
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = memberName,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Loan Amount: ₹${loan.loanAmount}",
                        color = SecondaryText
                    )

                    Text(
                        text = "Remaining: ₹${loan.remainingAmount}",
                        color = SecondaryText
                    )

                    Text(
                        text = "Due Date: ${dateFormat.format(Date(loan.dueDate))}",
                        color = SecondaryText
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = when {
                        isOverdue -> MaterialTheme.colorScheme.error
                        loan.status == "Completed" -> SuccessGreen
                        else -> WarningOrange
                    }
                ) {
                    Text(
                        text = if (isOverdue) {
                            "Overdue"
                        } else {
                            loan.status
                        },
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 7.dp
                        ),
                        color = CardBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            LinearProgressIndicator(
                progress = {
                    animatedProgress
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp),
                color = PrimaryGreen,
                trackColor = LightGreen,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${(animatedProgress * 100).toInt()}% repaid • Paid ₹${loan.paidAmount}",
                color = SecondaryText
            )

            Text(
                text = if (daysRemaining >= 0) {
                    "$daysRemaining days remaining"
                } else {
                    "${abs(daysRemaining)} days overdue"
                },
                color = if (isOverdue) {
                    MaterialTheme.colorScheme.error
                } else {
                    SecondaryText
                }
            )
        }
    }
}

@Composable
fun LoanSummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
