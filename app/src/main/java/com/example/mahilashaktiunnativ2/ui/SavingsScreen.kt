package com.example.mahilashaktiunnativ2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mahilashaktiunnativ2.ui.theme.*

data class SavingsRecord(

    val memberName: String,
    val amount: String,
    val date: String,
    val status: String
)

@Composable
fun SavingsScreen() {

    var searchText by remember {

        mutableStateOf("")
    }

    val savingsRecords = listOf(

        SavingsRecord(
            "Sangeeta Devi",
            "₹2,000",
            "12 May 2026",
            "Paid"
        ),

        SavingsRecord(
            "Lakshmi Bai",
            "₹1,500",
            "10 May 2026",
            "Pending"
        ),

        SavingsRecord(
            "Anita Kumari",
            "₹3,200",
            "08 May 2026",
            "Paid"
        ),

        SavingsRecord(
            "Radha Amma",
            "₹1,000",
            "05 May 2026",
            "Overdue"
        )
    )

    val filteredRecords = savingsRecords.filter {

        it.memberName.contains(
            searchText,
            ignoreCase = true
        )
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

                text = "Savings Tracker",

                style =
                    MaterialTheme
                        .typography
                        .headlineSmall
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(

                text =
                    "Track SHG member savings and contributions.",

                color = SecondaryText
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Row(

                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)

            ) {

                SavingsSummaryCard(

                    title = "Total Savings",

                    value = "₹4.8L",

                    modifier =
                        Modifier.weight(1f)
                )

                SavingsSummaryCard(

                    title = "This Month",

                    value = "₹52,000",

                    modifier =
                        Modifier.weight(1f)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            OutlinedTextField(

                value = searchText,

                onValueChange = {

                    searchText = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                placeholder = {

                    Text("Search savings records")
                },

                leadingIcon = {

                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },

                shape =
                    RoundedCornerShape(18.dp),

                singleLine = true
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            LazyColumn(

                verticalArrangement =
                    Arrangement.spacedBy(14.dp)

            ) {

                items(filteredRecords) { record ->

                    SavingsCard(record)
                }
            }
        }

        FloatingActionButton(

            onClick = {

            },

            containerColor =
                PrimaryGreen,

            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)

        ) {

            Icon(
                Icons.Default.Add,
                contentDescription = null,

                tint = CardBackground
            )
        }
    }
}

@Composable
fun SavingsCard(record: SavingsRecord) {

    Card(

        shape =
            RoundedCornerShape(24.dp),

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

                verticalAlignment =
                    Alignment.CenterVertically,

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Column(

                    modifier =
                        Modifier.weight(1f)

                ) {

                    Text(

                        text = record.memberName,

                        style =
                            MaterialTheme
                                .typography
                                .titleLarge
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "Amount: ${record.amount}",

                        color = SecondaryText
                    )

                    Text(
                        text =
                            "Date: ${record.date}",

                        color = SecondaryText
                    )
                }

                Surface(

                    shape =
                        RoundedCornerShape(14.dp),

                    color = when (
                        record.status
                    ) {

                        "Paid" -> SuccessGreen

                        "Pending" -> WarningOrange

                        else -> PrimaryGreen
                    }

                ) {

                    Text(

                        text = record.status,

                        modifier =
                            Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 7.dp
                            ),

                        color = CardBackground
                    )
                }
            }
        }
    }
}

@Composable
fun SavingsSummaryCard(

    title: String,

    value: String,

    modifier: Modifier = Modifier

) {

    Card(

        modifier = modifier,

        shape =
            RoundedCornerShape(20.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    CardBackground
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

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(

                text = value,

                style =
                    MaterialTheme
                        .typography
                        .headlineSmall
            )
        }
    }
}