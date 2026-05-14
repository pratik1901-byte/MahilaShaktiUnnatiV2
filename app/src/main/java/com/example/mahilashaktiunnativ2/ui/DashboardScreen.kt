package com.example.mahilashaktiunnativ2.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mahilashaktiunnativ2.R
import coil.compose.AsyncImage
import com.example.mahilashaktiunnativ2.ui.database.AdminEntity
import com.example.mahilashaktiunnativ2.ui.database.AdminViewModel
import com.example.mahilashaktiunnativ2.ui.database.MemberViewModel
import com.example.mahilashaktiunnativ2.ui.database.NotificationEntity
import com.example.mahilashaktiunnativ2.ui.theme.AppBackground
import com.example.mahilashaktiunnativ2.ui.theme.CardBackground
import com.example.mahilashaktiunnativ2.ui.theme.LightGreen
import com.example.mahilashaktiunnativ2.ui.theme.PrimaryGreen
import com.example.mahilashaktiunnativ2.ui.theme.SecondaryText
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen() {
    val viewModel: MemberViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()
    val currentAdmin by adminViewModel.currentAdmin.collectAsState()
    val members by viewModel.members.collectAsState()
    val totalSavings by viewModel.totalSavings.collectAsState()
    val pendingCount by viewModel.pendingSavingsCount.collectAsState()
    val activeLoanCount by viewModel.activeLoanCount.collectAsState()
    val totalPendingLoans by viewModel.totalPendingLoans.collectAsState()
    val totalLoanAmount by viewModel.totalLoanAmount.collectAsState()
    val totalLoanPaidAmount by viewModel.totalLoanPaidAmount.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val context = LocalContext.current

    val totalMembers = members.size
    val activeMembers = members.count { it.savingsStatus == "Active" }
    val availableFund = (totalSavings ?: 0) - (totalPendingLoans ?: 0)
    val pendingPaymentCount = pendingCount + members.count { it.savingsAmount == 0 }
    val loanRecoveryRate =
        if ((totalLoanAmount ?: 0) > 0) {
            (((totalLoanPaidAmount ?: 0).toFloat() / (totalLoanAmount ?: 1).toFloat()) * 100).toInt()
        } else {
            0
        }

    val healthScore = listOf(
        if (availableFund > 10000) 2 else if (availableFund >= 0) 1 else -2,
        if (pendingPaymentCount == 0) 2 else if (pendingPaymentCount <= 2) 1 else if (pendingPaymentCount <= 5) 0 else -2,
        if ((totalLoanAmount ?: 0) == 0) 1 else if (loanRecoveryRate >= 80) 2 else if (loanRecoveryRate >= 60) 1 else if (loanRecoveryRate >= 35) 0 else -2,
        if (activeLoanCount <= 2) 1 else if (activeLoanCount <= 5) 0 else -1
    ).sum()

    val financialHealth = when {
        healthScore >= 6 -> "Highly Safe"
        healthScore >= 4 -> "Safe"
        healthScore >= 2 -> "Low Risk"
        healthScore >= 0 -> "Neutral"
        healthScore >= -2 -> "Medium Risk"
        else -> "High Risk"
    }
    val financialHealthColor = financialHealthColor(financialHealth)

    val summary = buildShgSummary(
        totalMembers = totalMembers,
        activeMembers = activeMembers,
        totalSavings = totalSavings ?: 0,
        activeLoans = activeLoanCount,
        availableFund = availableFund,
        pendingPayments = pendingPaymentCount,
        loanRecoveryRate = loanRecoveryRate,
        financialHealth = financialHealth
    )

    var showNotificationCenter by remember { mutableStateOf(false) }
    var showSidebar by remember { mutableStateOf(false) }

    if (showNotificationCenter) {
        NotificationCenterScreen(
            notifications = notifications,
            onBack = { showNotificationCenter = false }
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 0.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DashboardHeader(
                    adminName = currentAdmin?.name.orEmpty(),
                    onMenuClick = { showSidebar = true },
                    onNotificationClick = { showNotificationCenter = true },
                    onShareClick = {
                        shareShgSummaryPdf(context, summary)
                    }
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardMetricCard(
                            title = "Total Members",
                            value = totalMembers.toString(),
                            subtitle = "Registered Members",
                            accent = Color(0xFF1F9D55),
                            icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                        DashboardMetricCard(
                            title = "Active Members",
                            value = activeMembers.toString(),
                            subtitle = "Currently Active",
                            accent = Color(0xFF16A34A),
                            icon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardMetricCard(
                            title = "Total Savings",
                            value = "Rs ${totalSavings ?: 0}",
                            subtitle = "Collected Savings",
                            accent = Color(0xFF058669),
                            icon = { Icon(Icons.Default.AccountBox, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                        DashboardMetricCard(
                            title = "Active Loans",
                            value = activeLoanCount.toString(),
                            subtitle = "Ongoing Loans",
                            accent = Color(0xFF2563EB),
                            icon = { Icon(Icons.Default.Star, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardMetricCard(
                            title = "Available Fund",
                            value = "Rs $availableFund",
                            subtitle = "Savings - Loans",
                            accent = Color(0xFF7C3AED),
                            icon = { Icon(Icons.Default.Home, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                        DashboardMetricCard(
                            title = "Pending Payments",
                            value = pendingPaymentCount.toString(),
                            subtitle = "Savings Pending",
                            accent = Color(0xFFF97316),
                            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardMetricCard(
                            title = "Financial Health",
                            value = financialHealth,
                            subtitle = "Risk Indicator",
                            accent = financialHealthColor,
                            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                        DashboardMetricCard(
                            title = "Loan Recovery",
                            value = "$loanRecoveryRate%",
                            subtitle = "Repayment Progress",
                            accent = Color(0xFF7C3AED),
                            icon = { Icon(Icons.Default.AccountBox, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                FinancialInsightsCard(
                    loanRecoveryRate = loanRecoveryRate,
                    financialHealth = financialHealth,
                    financialHealthColor = financialHealthColor,
                    onShareClick = {
                        shareShgSummaryPdf(context, summary)
                    }
                )
            }
        }

        if (showSidebar) {
            DashboardSidebar(
                admin = currentAdmin,
                onClose = { showSidebar = false },
                onLogout = {
                    showSidebar = false
                    adminViewModel.logout()
                },
                onAdminUpdated = { updatedAdmin ->
                    adminViewModel.updateAdmin(updatedAdmin)
                },
                onExit = { (context as? Activity)?.finish() }
            )
        }
    }
}

@Composable
fun DashboardHeader(
    adminName: String,
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val generatedAt = remember {
        SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        ).format(Date())
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .shadow(5.dp, RoundedCornerShape(26.dp), clip = false)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF19A66A),
                        Color(0xFF0F7A4D),
                        Color(0xFF075C38),
                        Color(0xFF044626)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SmoothIconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
            }

            Row {
                SmoothIconButton(onClick = onShareClick) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                }
                SmoothIconButton(onClick = onNotificationClick) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 54.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mahila-Shakti Unnati",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Digital Accountant for SHGs",
                color = Color.White.copy(alpha = 0.86f),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, end = 20.dp, bottom = 18.dp)
        ) {
            Text(
                text = "Welcome, Superwoman ${adminName.ifBlank { "Admin" }}",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = generatedAt,
                color = Color.White.copy(alpha = 0.86f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SmoothIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(120),
        label = "smooth_icon_button"
    )

    IconButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        content()
    }
}

@Composable
fun DashboardMetricCard(
    title: String,
    value: String,
    subtitle: String,
    accent: Color,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(118.dp)
            .shadow(3.dp, RoundedCornerShape(16.dp), clip = false)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(22.dp)) {
                    androidx.compose.runtime.CompositionLocalProvider(
                        androidx.compose.material3.LocalContentColor provides accent
                    ) {
                        icon()
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = accent,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun FinancialInsightsCard(
    loanRecoveryRate: Int,
    financialHealth: String,
    financialHealthColor: Color,
    onShareClick: () -> Unit
) {
    val animatedRecovery by animateFloatAsState(
        targetValue = (loanRecoveryRate / 100f).coerceIn(0f, 1f),
        animationSpec = tween(700),
        label = "dashboard_recovery"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(8.dp), clip = false),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "SHG Financial Insights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Loan Recovery: $loanRecoveryRate%",
                color = SecondaryText
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { animatedRecovery },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .clip(RoundedCornerShape(50)),
                trackColor = LightGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Financial Health: $financialHealth",
                color = financialHealthColor,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(14.dp))
            SmoothButton(
                onClick = onShareClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share PDF Summary")
            }
        }
    }
}

@Composable
fun SmoothButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(120),
        label = "smooth_button"
    )

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        colors = colors,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        content()
    }
}

@Composable
fun NotificationCenterScreen(
    notifications: List<NotificationEntity>,
    onBack: () -> Unit
) {
    val dayFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val today = dayFormat.format(Date())
    val todaysNotifications = notifications.filter {
        dayFormat.format(Date(it.timestamp)) == today
    }
    val notificationsByDate = notifications.groupBy {
        dayFormat.format(Date(it.timestamp))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Notification Centre",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (todaysNotifications.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Text(
                    text = "No new messages for today.",
                    modifier = Modifier.padding(20.dp),
                    color = SecondaryText
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Text(
                text = "Today",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (notifications.isEmpty()) {
            Text(
                text = "Notification history will appear here after member, savings, loan or repayment activity.",
                color = SecondaryText
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                notificationsByDate.forEach { (dateLabel, dateNotifications) ->
                    item {
                        Text(
                            text = dateLabel,
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryGreen
                        )
                    }
                    items(dateNotifications) { notification ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = notification.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notification.message,
                                    color = SecondaryText
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${notification.eventType} - ${timeFormat.format(Date(notification.timestamp))}",
                                    color = SecondaryText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun buildShgSummary(
    totalMembers: Int,
    activeMembers: Int,
    totalSavings: Int,
    activeLoans: Int,
    availableFund: Int,
    pendingPayments: Int,
    loanRecoveryRate: Int,
    financialHealth: String
): String {
    val dateText =
        SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        ).format(Date())

    return """
        MahilaShaktiUnnati - SHG Summary
        Date: $dateText

        Members
        Total members: $totalMembers
        Active members: $activeMembers

        Savings and Fund
        Total savings: Rs $totalSavings
        Available fund: Rs $availableFund
        Pending payments: $pendingPayments

        Loans
        Active loans: $activeLoans
        Loan recovery: $loanRecoveryRate%

        Financial health: $financialHealth
    """.trimIndent()
}

fun financialHealthColor(
    financialHealth: String
): Color {
    return when (financialHealth) {
        "Highly Safe" -> Color(0xFF047857)
        "Safe" -> Color(0xFF16A34A)
        "Low Risk" -> Color(0xFF65A30D)
        "Neutral" -> Color(0xFFEAB308)
        "Medium Risk" -> Color(0xFFF97316)
        else -> Color(0xFFDC2626)
    }
}

fun shareShgSummaryPdf(
    context: Context,
    summary: String
) {
    try {
        val pdfFile = createSummaryPdf(context, summary)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            pdfFile
        )

        val whatsappIntent =
            Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "MahilaShaktiUnnati SHG Summary")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setPackage("com.whatsapp")
            }

        try {
            context.startActivity(whatsappIntent)
        } catch (_: ActivityNotFoundException) {
            val shareIntent =
                Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "MahilaShaktiUnnati SHG Summary")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

            context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    "Share SHG summary PDF"
                )
            )
        }
    } catch (_: Exception) {
        Toast.makeText(
            context,
            "Unable to create PDF summary.",
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun createSummaryPdf(
    context: Context,
    summary: String
): File {
    val document = PdfDocument()
    val pageInfo =
        PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page =
        document.startPage(pageInfo)
    val canvas =
        page.canvas

    val darkGreen =
        android.graphics.Color.rgb(20, 104, 72)
    val primaryGreen =
        android.graphics.Color.rgb(31, 157, 85)
    val softGreen =
        android.graphics.Color.rgb(231, 247, 238)
    val ink =
        android.graphics.Color.rgb(32, 39, 44)
    val muted =
        android.graphics.Color.rgb(91, 103, 112)
    val border =
        android.graphics.Color.rgb(222, 230, 224)

    val headerPaint =
        Paint().apply {
            color = darkGreen
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    val cardPaint =
        Paint().apply {
            color = android.graphics.Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    val cardStrokePaint =
        Paint().apply {
            color = border
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
            isAntiAlias = true
        }
    val accentPaint =
        Paint().apply {
            color = softGreen
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    val titlePaint =
        Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
    val taglinePaint =
        Paint().apply {
            color = android.graphics.Color.rgb(224, 244, 232)
            textSize = 12f
            isAntiAlias = true
        }
    val bodyPaint =
        Paint().apply {
            color = ink
            textSize = 14f
            isAntiAlias = true
        }
    val sectionPaint =
        Paint().apply {
            color = ink
            textSize = 17f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
    val labelPaint =
        Paint().apply {
            color = muted
            textSize = 11f
            isAntiAlias = true
        }
    val valuePaint =
        Paint().apply {
            color = ink
            textSize = 19f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
    val greenValuePaint =
        Paint(valuePaint).apply {
            color = darkGreen
        }
    val footerPaint =
        Paint().apply {
            color = muted
            textSize = 9.5f
            isAntiAlias = true
        }

    val summaryValues =
        mapOf(
            "Date" to summaryValue(summary, "Date"),
            "Total Members" to summaryValue(summary, "Total members"),
            "Active Members" to summaryValue(summary, "Active members"),
            "Total Savings" to summaryValue(summary, "Total savings"),
            "Available Fund" to summaryValue(summary, "Available fund"),
            "Pending Payments" to summaryValue(summary, "Pending payments"),
            "Active Loans" to summaryValue(summary, "Active loans"),
            "Loan Recovery" to summaryValue(summary, "Loan recovery"),
            "Financial Health" to summaryValue(summary, "Financial health")
        )

    canvas.drawColor(android.graphics.Color.rgb(249, 252, 250))
    canvas.drawRect(0f, 0f, 595f, 132f, headerPaint)

    val logo =
        BitmapFactory.decodeResource(context.resources, R.drawable.msu_logo)
    if (logo != null) {
        val scaledLogo =
            android.graphics.Bitmap.createScaledBitmap(logo, 58, 58, true)
        canvas.drawBitmap(scaledLogo, 40f, 34f, null)
    } else {
        canvas.drawCircle(69f, 63f, 29f, accentPaint)
    }

    canvas.drawText("MahilaShaktiUnnati", 112f, 55f, titlePaint)
    canvas.drawText("Empowering Women Through Unity", 112f, 77f, taglinePaint)
    canvas.drawText("Women Self Help Group Financial Statement", 112f, 98f, taglinePaint)

    val badgePaint =
        Paint().apply {
            color = android.graphics.Color.rgb(236, 253, 245)
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    val badgeTextPaint =
        Paint().apply {
            color = darkGreen
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
    canvas.drawRoundRect(RectF(414f, 38f, 540f, 72f), 16f, 16f, badgePaint)
    canvas.drawText("SHG SUMMARY", 436f, 60f, badgeTextPaint)

    canvas.drawText("Generated on: ${summaryValues["Date"]}", 40f, 166f, labelPaint)
    canvas.drawText("Official digital ledger summary for group savings, loans and repayment tracking.", 40f, 186f, bodyPaint)

    canvas.drawText("Financial Snapshot", 40f, 226f, sectionPaint)
    drawPdfMetricCard(canvas, RectF(40f, 246f, 270f, 324f), "Total Savings", summaryValues["Total Savings"].orEmpty(), "All-time member savings", primaryGreen, cardPaint, cardStrokePaint, labelPaint, greenValuePaint, accentPaint)
    drawPdfMetricCard(canvas, RectF(300f, 246f, 555f, 324f), "Available Fund", summaryValues["Available Fund"].orEmpty(), "Savings minus active pending loans", primaryGreen, cardPaint, cardStrokePaint, labelPaint, greenValuePaint, accentPaint)

    drawPdfMetricCard(canvas, RectF(40f, 344f, 270f, 422f), "Total Members", summaryValues["Total Members"].orEmpty(), "${summaryValues["Active Members"]} active members", primaryGreen, cardPaint, cardStrokePaint, labelPaint, valuePaint, accentPaint)
    drawPdfMetricCard(canvas, RectF(300f, 344f, 555f, 422f), "Financial Health", summaryValues["Financial Health"].orEmpty(), "Based on fund, recovery and pending load", primaryGreen, cardPaint, cardStrokePaint, labelPaint, greenValuePaint, accentPaint)

    canvas.drawText("Loan Position", 40f, 470f, sectionPaint)
    drawPdfTableRow(canvas, 40f, 494f, "Active Loans", summaryValues["Active Loans"].orEmpty(), bodyPaint, valuePaint, border)
    drawPdfTableRow(canvas, 40f, 536f, "Pending Payments", summaryValues["Pending Payments"].orEmpty(), bodyPaint, valuePaint, border)
    drawPdfTableRow(canvas, 40f, 578f, "Loan Recovery", summaryValues["Loan Recovery"].orEmpty(), bodyPaint, greenValuePaint, border)

    canvas.drawText("Operational Notes", 40f, 646f, sectionPaint)
    canvas.drawText("- Member directory, savings entries and loan records are maintained digitally.", 56f, 676f, bodyPaint)
    canvas.drawText("- Available fund helps avoid lending more than the SHG can support.", 56f, 700f, bodyPaint)
    canvas.drawText("- Loan recovery percentage updates when repayments are recorded.", 56f, 724f, bodyPaint)

    val footerY = 802f
    canvas.drawLine(40f, footerY - 20f, 555f, footerY - 20f, cardStrokePaint)
    canvas.drawText("Prepared by MahilaShaktiUnnati | Digital SHG Management Ledger", 40f, footerY, footerPaint)
    canvas.drawText("This PDF is generated from app records and intended for SHG review/share via WhatsApp.", 40f, footerY + 18f, footerPaint)

    document.finishPage(page)

    val pdfFile =
        File(context.cacheDir, "MahilaShaktiUnnati_SHG_Summary.pdf")

    FileOutputStream(pdfFile).use { output ->
        document.writeTo(output)
    }

    document.close()
    return pdfFile
}

fun drawPdfMetricCard(
    canvas: android.graphics.Canvas,
    rect: RectF,
    title: String,
    value: String,
    subtitle: String,
    accentColor: Int,
    cardPaint: Paint,
    cardStrokePaint: Paint,
    labelPaint: Paint,
    valuePaint: Paint,
    accentPaint: Paint
) {
    canvas.drawRoundRect(rect, 18f, 18f, cardPaint)
    canvas.drawRoundRect(rect, 18f, 18f, cardStrokePaint)
    val iconRect =
        RectF(rect.left + 16f, rect.top + 18f, rect.left + 52f, rect.top + 54f)
    canvas.drawOval(iconRect, accentPaint)
    val iconPaint =
        Paint().apply {
            color = accentColor
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
    canvas.drawText("M", iconRect.centerX(), iconRect.centerY() + 6f, iconPaint)
    canvas.drawText(title, rect.left + 66f, rect.top + 28f, labelPaint)
    canvas.drawText(value.ifBlank { "-" }.take(26), rect.left + 66f, rect.top + 53f, valuePaint)
    canvas.drawText(subtitle.take(42), rect.left + 66f, rect.top + 70f, labelPaint)
}

fun drawPdfTableRow(
    canvas: android.graphics.Canvas,
    left: Float,
    top: Float,
    label: String,
    value: String,
    labelPaint: Paint,
    valuePaint: Paint,
    borderColor: Int
) {
    val borderPaint =
        Paint().apply {
            color = borderColor
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    canvas.drawRoundRect(RectF(left, top, 555f, top + 34f), 10f, 10f, borderPaint)
    canvas.drawText(label, left + 16f, top + 23f, labelPaint)
    val valuePaintRight =
        Paint(valuePaint).apply {
            textAlign = Paint.Align.RIGHT
        }
    canvas.drawText(value.ifBlank { "-" }, 535f, top + 23f, valuePaintRight)
}

fun summaryValue(
    summary: String,
    label: String
): String {
    return summary
        .lineSequence()
        .firstOrNull {
            it.trim().startsWith("$label:")
        }
        ?.substringAfter(":")
        ?.trim()
        .orEmpty()
}

@Composable
fun DashboardSidebar(
    admin: AdminEntity?,
    onClose: () -> Unit,
    onLogout: () -> Unit,
    onAdminUpdated: (AdminEntity) -> Unit,
    onExit: () -> Unit
) {
    var showProfile by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    if (showProfile && admin != null) {
        AdminProfileDialog(
            admin = admin,
            onDismiss = {
                showProfile = false
            }
        )
    }

    if (showSettings && admin != null) {
        AdminSettingsDialog(
            admin = admin,
            onDismiss = {
                showSettings = false
            },
            onSave = { updatedAdmin ->
                onAdminUpdated(updatedAdmin)
                showSettings = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.28f))
            .clickable { onClose() }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(288.dp)
                .clickable { },
            color = CardBackground,
            shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = admin?.name ?: "Admin",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = admin?.occupation ?: "SHG Management",
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(24.dp))

                SidebarAction(
                    title = "Profile",
                    subtitle = "View admin profile",
                    onClick = {
                        showProfile = true
                    }
                )
                SidebarAction(
                    title = "Settings",
                    subtitle = "Manage admin profile settings",
                    onClick = {
                        showSettings = true
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                SidebarAction(
                    title = "Logout",
                    subtitle = "Sign out from this device",
                    onClick = onLogout
                )
                Button(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Exit App")
                }
            }
        }
    }
}

@Composable
fun SidebarAction(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = subtitle,
            color = SecondaryText,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun AdminProfileDialog(
    admin: AdminEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        },
        title = {
            Text("Admin Profile")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AdminProfilePhoto(photoUri = admin.photoUri)

                Spacer(modifier = Modifier.height(16.dp))

                AdminProfileRow("Name", admin.name)
                AdminProfileRow("Phone", admin.phoneNumber)
                AdminProfileRow("Date of Birth", admin.dateOfBirth)
                AdminProfileRow("Age", "${admin.age} years")
                AdminProfileRow("From", admin.village)
                AdminProfileRow("Address", admin.address.ifBlank { "-" })
                AdminProfileRow("SHG Role", admin.occupation)
            }
        }
    )
}

@Composable
fun AdminSettingsDialog(
    admin: AdminEntity,
    onDismiss: () -> Unit,
    onSave: (AdminEntity) -> Unit
) {
    val context =
        LocalContext.current

    var name by remember { mutableStateOf(admin.name) }
    var phoneNumber by remember { mutableStateOf(admin.phoneNumber) }
    var password by remember { mutableStateOf(admin.password) }
    var dateOfBirth by remember { mutableStateOf(admin.dateOfBirth) }
    var village by remember { mutableStateOf(admin.village) }
    var address by remember { mutableStateOf(admin.address) }
    var occupation by remember { mutableStateOf(admin.occupation) }
    var photoUri by remember { mutableStateOf(admin.photoUri) }
    var errorMessage by remember { mutableStateOf("") }

    val cropLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                photoUri = result.data?.let { UCrop.getOutput(it) }?.toString()
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                val destinationUri =
                    Uri.fromFile(
                        File.createTempFile(
                            "admin_profile_update",
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
                    val age =
                        calculateAgeFromDate(dateOfBirth)

                    errorMessage =
                        adminValidationMessage(
                            name = name,
                            phoneNumber = phoneNumber,
                            password = password,
                            dateOfBirth = dateOfBirth,
                            age = age,
                            village = village,
                            occupation = occupation
                        )

                    if (errorMessage.isBlank()) {
                        onSave(
                            admin.copy(
                                name = name.trim(),
                                phoneNumber = phoneNumber.trim(),
                                password = password,
                                dateOfBirth = dateOfBirth,
                                age = age,
                                village = village.trim(),
                                address = address.trim(),
                                occupation = occupation.trim(),
                                photoUri = photoUri
                            )
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text("Profile Settings")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(540.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AdminPhotoPicker(
                    photoUri = photoUri,
                    onPickPhoto = {
                        galleryLauncher.launch("image/*")
                    }
                )

                AdminInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Admin Name"
                )
                AdminInputField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = "Phone Number",
                    keyboardType = KeyboardType.Phone
                )
                AdminInputField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    keyboardType = KeyboardType.Password,
                    isPassword = true
                )
                MemberDateInputField(
                    value = dateOfBirth,
                    onDateSelected = { dateOfBirth = it },
                    label = "Date of Birth"
                )
                AdminInputField(
                    value = village,
                    onValueChange = { village = it },
                    label = "Village / City"
                )
                AdminInputField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Address"
                )
                AdminInputField(
                    value = occupation,
                    onValueChange = { occupation = it },
                    label = "SHG Role / Occupation"
                )

                if (errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun AdminProfilePhoto(
    photoUri: String?
) {
    if (photoUri != null) {
        AsyncImage(
            model = photoUri,
            contentDescription = null,
            modifier = Modifier
                .size(116.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(116.dp)
                .clip(CircleShape)
                .background(LightGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(46.dp)
            )
        }
    }
}

@Composable
fun AdminProfileRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = SecondaryText
        )
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 12.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
