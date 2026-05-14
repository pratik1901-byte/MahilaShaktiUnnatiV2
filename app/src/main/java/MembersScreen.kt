package com.example.mahilashaktiunnativ2.ui

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mahilashaktiunnativ2.ui.database.MemberEntity
import com.example.mahilashaktiunnativ2.ui.database.MemberViewModel
import com.example.mahilashaktiunnativ2.ui.theme.AppBackground
import com.example.mahilashaktiunnativ2.ui.theme.CardBackground
import com.example.mahilashaktiunnativ2.ui.theme.LightGreen
import com.example.mahilashaktiunnativ2.ui.theme.PrimaryGreen
import com.example.mahilashaktiunnativ2.ui.theme.SecondaryText
import com.example.mahilashaktiunnativ2.ui.theme.SuccessGreen
import com.example.mahilashaktiunnativ2.ui.theme.WarningOrange
import com.yalantis.ucrop.UCrop
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MembersScreen() {
    val viewModel: MemberViewModel = viewModel()
    val members by viewModel.members.collectAsState()
    val archivedMembers by viewModel.archivedMembers.collectAsState()
    val loans by viewModel.allLoans.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showEligibilityDialog by remember { mutableStateOf(false) }
    var showArchive by remember { mutableStateOf(false) }
    var memberToArchive by remember { mutableStateOf<MemberEntity?>(null) }
    var selectedMember by remember { mutableStateOf<MemberEntity?>(null) }
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredMembers = members.filter { member ->
        val matchesSearch = member.name.contains(searchText, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Active" -> member.savingsStatus == "Active"
            "Pending" -> member.savingsAmount == 0
            "High Savings" -> member.savingsAmount >= 5000
            else -> true
        }

        matchesSearch && matchesFilter
    }

    if (showArchive) {
        ArchivedMembersScreen(
            archivedMembers = archivedMembers,
            onBack = {
                showArchive = false
            },
            onRestore = { member ->
                viewModel.restoreMember(member)
            }
        )
    } else if (selectedMember != null) {
        MemberProfileScreen(
            member = selectedMember!!,
            onBack = {
                selectedMember = null
            }
        )
    } else {
        if (showEligibilityDialog) {
            AlertDialog(
                onDismissRequest = {
                    showEligibilityDialog = false
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showEligibilityDialog = false
                            showDialog = true
                        }
                    ) {
                        Text("Continue")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = {
                            showEligibilityDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                },
                title = {
                    Text("Women SHG Eligibility")
                },
                text = {
                    Text(
                        "This platform is exclusively intended for women members of Self Help Groups (SHGs). Only eligible women members should register accounts."
                    )
                }
            )
        }

        if (showDialog) {
            AddMemberDialog(
                existingMembers = members,
                onDismiss = {
                    showDialog = false
                },
                onAddMember = { member ->
                    viewModel.addMember(member)
                    showDialog = false
                }
            )
        }

        memberToArchive?.let { member ->
            AlertDialog(
                onDismissRequest = {
                    memberToArchive = null
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.archiveMember(member)
                            memberToArchive = null
                        }
                    ) {
                        Text("Yes, Archive")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = {
                            memberToArchive = null
                        }
                    ) {
                        Text("Cancel")
                    }
                },
                title = {
                    Text("Are you sure?")
                },
                text = {
                    Text(
                        "Are you sure you want to delete/archive ${member.name}? This will move her to Past Members instead of permanently deleting her savings and loan history."
                    )
                }
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
                    text = "Member Directory",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedButton(
                    onClick = {
                        showArchive = true
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        Icons.Default.Archive,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Past Members (${archivedMembers.size})")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "All",
                        onClick = {
                            selectedFilter = "All"
                        },
                        label = {
                            Text("All")
                        }
                    )

                    FilterChip(
                        selected = selectedFilter == "Active",
                        onClick = {
                            selectedFilter = "Active"
                        },
                        label = {
                            Text("Active")
                        }
                    )

                    FilterChip(
                        selected = selectedFilter == "Pending",
                        onClick = {
                            selectedFilter = "Pending"
                        },
                        label = {
                            Text("Pending")
                        }
                    )

                    FilterChip(
                        selected = selectedFilter == "High Savings",
                        onClick = {
                            selectedFilter = "High Savings"
                        },
                        label = {
                            Text("High Savings")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "${filteredMembers.size} members found",
                    color = SecondaryText
                )

                Text(
                    text = "Manage SHG members, savings and loans.",
                    color = SecondaryText
                )

                if (selectedFilter == "High Savings") {
                    Text(
                        text = "High Savings shows members with total savings of ₹5,000 or more.",
                        color = SecondaryText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (selectedFilter == "Pending") {
                    Text(
                        text = "Pending shows members who have not recorded any savings yet.",
                        color = SecondaryText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Search members")
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

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        bottom = 96.dp
                    )
                ) {
                    items(
                        items = filteredMembers,
                        key = { it.id }
                    ) { member ->
                        MemberCard(
                            member = member,
                            loanStatus = if (
                                loans.any {
                                    it.memberId == member.memberId &&
                                            it.status == "Active"
                                }
                            ) {
                                "Active Loan"
                            } else {
                                "No Active Loan"
                            },
                            onClick = {
                                selectedMember = member
                            },
                            onArchive = {
                                memberToArchive = member
                            }
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    showEligibilityDialog = true
                },
                containerColor = PrimaryGreen,
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
}

@Composable
fun AddMemberDialog(
    existingMembers: List<MemberEntity>,
    onDismiss: () -> Unit,
    onAddMember: (MemberEntity) -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var aadhaarNumber by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var joiningDate by remember { mutableStateOf("") }
    var weeklySavingAmount by remember { mutableStateOf("") }
    var nomineeName by remember { mutableStateOf("") }
    var nomineePhoneNumber by remember { mutableStateOf("") }
    var nomineeRelation by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val memberId = remember {
        "MSU-${System.currentTimeMillis().toString().takeLast(5)}"
    }

    val cropLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                photoUri = result.data?.let { UCrop.getOutput(it) }
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                val destinationUri =
                    Uri.fromFile(
                        java.io.File.createTempFile(
                            "cropped_image",
                            ".jpg",
                            context.cacheDir
                        )
                    )

                val cropIntent =
                    UCrop.of(it, destinationUri)
                        .withAspectRatio(1f, 1f)
                        .withMaxResultSize(800, 800)
                        .withOptions(safeCropOptions(context))
                        .getIntent(context)

                cropLauncher.launch(cropIntent)
            }
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val validPhone =
                        phoneNumber.length == 10 &&
                                phoneNumber.all { it.isDigit() }

                    val validNomineePhone =
                        nomineePhoneNumber.length == 10 &&
                                nomineePhoneNumber.all { it.isDigit() }

                    val validAadhaar =
                        aadhaarNumber.length == 12 &&
                                aadhaarNumber.all { it.isDigit() }

                    val calculatedAge =
                        calculateAgeFromDate(dateOfBirth)

                    val validAge =
                        calculatedAge in 18..80

                    val duplicateMember =
                        existingMembers.any {
                            it.phoneNumber == phoneNumber ||
                                    it.aadhaarNumber == aadhaarNumber
                        }

                    if (
                        name.isBlank() ||
                        dateOfBirth.isBlank() ||
                        village.isBlank() ||
                        weeklySavingAmount.isBlank()
                    ) {
                        errorMessage = "Please fill all mandatory fields"
                    } else if (!validAge) {
                        errorMessage = "Age must be between 18 and 80"
                    } else if (!validPhone) {
                        errorMessage = "Enter valid 10-digit phone number"
                    } else if (!validAadhaar) {
                        errorMessage = "Enter valid 12-digit Aadhaar number"
                    } else if (!validNomineePhone) {
                        errorMessage = "Enter valid nominee phone number"
                    } else if (duplicateMember) {
                        errorMessage =
                            "Member already exists with same Aadhaar or phone number"
                    } else {
                        val member = MemberEntity(
                            name = name,
                            memberId = memberId,
                            age = calculatedAge,
                            dateOfBirth = dateOfBirth,
                            phoneNumber = phoneNumber,
                            aadhaarNumber = aadhaarNumber,
                            village = village,
                            address = address,
                            joiningDate = joiningDate,
                            weeklySavingAmount = weeklySavingAmount.toIntOrNull() ?: 0,
                            savingsStatus = "Active",
                            savingsAmount = 0,
                            loansTaken = "No Loans",
                            nomineeName = nomineeName,
                            nomineePhoneNumber = nomineePhoneNumber,
                            nomineeRelation = nomineeRelation,
                            photoUri = photoUri?.toString()
                        )

                        onAddMember(member)
                    }
                }
            ) {
                Text("Add Member")
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
            Text("Register New Member")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(550.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (photoUri != null) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(LightGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(46.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = {
                                galleryLauncher.launch("image/*")
                            }
                        ) {
                            Text("Upload Photo")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                MemberInputField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage =
                            memberDraftValidationMessage(
                                name,
                                dateOfBirth,
                                phoneNumber,
                                aadhaarNumber,
                                village,
                                weeklySavingAmount,
                                nomineePhoneNumber
                            )
                    },
                    label = "Full Name"
                )

                MemberDateInputField(
                    value = dateOfBirth,
                    onDateSelected = {
                        dateOfBirth = it
                        errorMessage =
                            memberDraftValidationMessage(
                                name,
                                dateOfBirth,
                                phoneNumber,
                                aadhaarNumber,
                                village,
                                weeklySavingAmount,
                                nomineePhoneNumber
                            )
                    },
                    label = "Date of Birth"
                )

                MemberInputField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        errorMessage =
                            memberDraftValidationMessage(
                                name,
                                dateOfBirth,
                                phoneNumber,
                                aadhaarNumber,
                                village,
                                weeklySavingAmount,
                                nomineePhoneNumber
                            )
                    },
                    label = "Phone Number",
                    keyboardType = KeyboardType.Phone
                )

                MemberInputField(
                    value = aadhaarNumber,
                    onValueChange = {
                        aadhaarNumber = it
                        errorMessage =
                            memberDraftValidationMessage(
                                name,
                                dateOfBirth,
                                phoneNumber,
                                aadhaarNumber,
                                village,
                                weeklySavingAmount,
                                nomineePhoneNumber
                            )
                    },
                    label = "Aadhaar Number",
                    keyboardType = KeyboardType.Number
                )

                MemberInputField(
                    value = village,
                    onValueChange = {
                        village = it
                        errorMessage =
                            memberDraftValidationMessage(
                                name,
                                dateOfBirth,
                                phoneNumber,
                                aadhaarNumber,
                                village,
                                weeklySavingAmount,
                                nomineePhoneNumber
                            )
                    },
                    label = "Village"
                )

                MemberInputField(
                    value = address,
                    onValueChange = {
                        address = it
                    },
                    label = "Full Address"
                )

                MemberDateInputField(
                    value = joiningDate,
                    onDateSelected = {
                        joiningDate = it
                    },
                    label = "Joining Date"
                )

                MemberInputField(
                    value = weeklySavingAmount,
                    onValueChange = {
                        weeklySavingAmount = it
                        errorMessage =
                            memberDraftValidationMessage(
                                name,
                                dateOfBirth,
                                phoneNumber,
                                aadhaarNumber,
                                village,
                                weeklySavingAmount,
                                nomineePhoneNumber
                            )
                    },
                    label = "Weekly Savings Amount",
                    keyboardType = KeyboardType.Number
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
                        errorMessage =
                            memberDraftValidationMessage(
                                name,
                                dateOfBirth,
                                phoneNumber,
                                aadhaarNumber,
                                village,
                                weeklySavingAmount,
                                nomineePhoneNumber
                            )
                    },
                    label = "Nominee Phone Number",
                    keyboardType = KeyboardType.Phone
                )

                MemberInputField(
                    value = nomineeRelation,
                    onValueChange = {
                        nomineeRelation = it
                    },
                    label = "Nominee Relation"
                )

                AnimatedVisibility(
                    visible = errorMessage.isNotEmpty()
                ) {
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
fun MemberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )
}

@Composable
fun MemberDateInputField(
    value: String,
    onDateSelected: (String) -> Unit,
    label: String
) {
    val context = LocalContext.current
    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    val calendar = remember {
        Calendar.getInstance()
    }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }

                onDateSelected(dateFormatter.format(selectedCalendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedButton(
        onClick = {
            datePickerDialog.show()
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = PrimaryGreen
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (value.isBlank()) label else value,
                color = if (value.isBlank()) SecondaryText else PrimaryGreen
            )

            Text(
                text = "Select",
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun MemberCard(
    member: MemberEntity,
    loanStatus: String,
    onClick: () -> Unit,
    onArchive: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(120),
        label = "member_card_press"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            )
            .animateContentSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            },
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (member.photoUri != null) {
                    AsyncImage(
                        model = member.photoUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(LightGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "ID: ${member.memberId}",
                        color = SecondaryText
                    )

                    Text(
                        text = "Village: ${member.village}",
                        color = SecondaryText
                    )

                    Text(
                        text = "Weekly: ₹${member.weeklySavingAmount}",
                        color = SecondaryText
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (member.savingsStatus == "Active") {
                        SuccessGreen
                    } else {
                        WarningOrange
                    }
                ) {
                    Text(
                        text = member.savingsStatus,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 7.dp
                        ),
                        color = CardBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MemberInfoCard(
                    title = "Savings",
                    value = "₹${member.savingsAmount}",
                    modifier = Modifier.weight(1f)
                )

                MemberInfoCard(
                    title = "Loans",
                    value = loanStatus,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onArchive,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete / Archive")
            }
        }
    }
}

@Composable
fun MemberInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightGreen
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = title,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun ArchivedMembersScreen(
    archivedMembers: List<MemberEntity>,
    onBack: () -> Unit,
    onRestore: (MemberEntity) -> Unit
) {
    val dateFormat = remember {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Back")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Past Members",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (archivedMembers.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Text(
                    text = "No past members archived yet.",
                    modifier = Modifier.padding(20.dp),
                    color = SecondaryText
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = archivedMembers,
                    key = { it.id }
                ) { member ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Archive,
                                    contentDescription = null,
                                    tint = PrimaryGreen
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = member.name,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = "ID: ${member.memberId} | Village: ${member.village}",
                                        color = SecondaryText
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Archived: ${
                                    member.archivedDate?.let {
                                        dateFormat.format(java.util.Date(it))
                                    } ?: "Not recorded"
                                }",
                                color = SecondaryText
                            )

                            if (member.archiveReason.isNotBlank()) {
                                Text(
                                    text = "Reason: ${member.archiveReason}",
                                    color = SecondaryText
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = {
                                    onRestore(member)
                                },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Restore,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Restore Member")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun calculateAgeFromDate(
    dateText: String
): Int {
    return try {
        val formatter =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val birthDate =
            formatter.parse(dateText) ?: return 0

        val birthCalendar =
            Calendar.getInstance().apply {
                time = birthDate
            }

        val today =
            Calendar.getInstance()

        var age =
            today.get(Calendar.YEAR) -
                    birthCalendar.get(Calendar.YEAR)

        val birthdayThisYear =
            Calendar.getInstance().apply {
                set(
                    today.get(Calendar.YEAR),
                    birthCalendar.get(Calendar.MONTH),
                    birthCalendar.get(Calendar.DAY_OF_MONTH)
                )
            }

        if (today.before(birthdayThisYear)) {
            age--
        }

        age
    } catch (_: Exception) {
        0
    }
}

fun memberDraftValidationMessage(
    name: String,
    dateOfBirth: String,
    phoneNumber: String,
    aadhaarNumber: String,
    village: String,
    weeklySavingAmount: String,
    nomineePhoneNumber: String
): String {
    val age =
        calculateAgeFromDate(dateOfBirth)

    return when {
        name.isBlank() -> "Enter member full name"
        dateOfBirth.isBlank() -> "Select date of birth"
        age !in 18..80 -> "Member age must be between 18 and 80"
        phoneNumber.isNotBlank() &&
                (phoneNumber.length != 10 || !phoneNumber.all { it.isDigit() }) ->
            "Enter a valid 10-digit phone number"
        aadhaarNumber.isNotBlank() &&
                (aadhaarNumber.length != 12 || !aadhaarNumber.all { it.isDigit() }) ->
            "Enter a valid 12-digit Aadhaar number"
        village.isBlank() -> "Enter village name"
        weeklySavingAmount.isNotBlank() &&
                weeklySavingAmount.toIntOrNull() == null ->
            "Weekly savings amount must be a number"
        nomineePhoneNumber.isNotBlank() &&
                (nomineePhoneNumber.length != 10 || !nomineePhoneNumber.all { it.isDigit() }) ->
            "Enter a valid 10-digit nominee phone number"
        else -> ""
    }
}
