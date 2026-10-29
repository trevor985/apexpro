package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.RevenueRecordEntity
import com.example.ui.components.HudMetricCard
import com.example.ui.components.LivePulseIndicator
import com.example.ui.components.RunRateProgressBar
import com.example.ui.components.SectionHeader
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CrimsonLoss
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonProfitGreen
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.SolarGold
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TeslaCyberBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.VentureViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: VentureViewModel,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val todayRevenue by viewModel.todayRevenue.collectAsState()
    val todayRecords by viewModel.todayRecords.collectAsState()
    val volatilityOverview by viewModel.marketVolatilityOverview.collectAsState()
    val isQuickAddOpen by viewModel.quickAddDialogOpen.collectAsState()

    var newTitle by remember { mutableStateOf("") }
    var newAmount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("SAAS") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Mission Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, NeonProfitGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.apex_hero_badge),
                            contentDescription = "Apex Emblem",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "APEX VENTURE OS",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Mission: $100 / Day",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
                LivePulseIndicator(label = "ONLINE", color = NeonProfitGreen)
            }
        }

        // Live Daily Run Rate Telemetry
        item {
            RunRateProgressBar(
                currentAmount = todayRevenue,
                targetAmount = 100.0
            )
        }

        // Action Buttons Row: Quick Add & Trigger Simulation
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.setQuickAddDialogOpen(true) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("log_revenue_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonProfitGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Log Revenue",
                        tint = SpaceBlack,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Log Cash Inflow",
                        color = SpaceBlack,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = {
                        // Simulate instant micro-sale: e.g. Gumroad digital prompt license $19
                        viewModel.logRevenue(
                            title = "AI Prompt License Direct Checkout",
                            amount = 19.0,
                            category = "PRODUCTS",
                            notes = "Instant stripe payment"
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("simulate_sale_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Simulate Sale",
                        tint = SolarGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+$19 Quick Sale",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // 3 Engine Telemetry Cards
        item {
            SectionHeader(title = "Active Cash Engines", badgeText = "3 RUNNING")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HudMetricCard(
                    title = "B2B SaaS",
                    value = "$42.50",
                    subValue = "+12% vs yday",
                    accentColor = NeonProfitGreen,
                    icon = {
                        Icon(
                            Icons.Default.CloudQueue,
                            contentDescription = "SaaS",
                            tint = NeonProfitGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "metric_saas"
                )
                HudMetricCard(
                    title = "Signals",
                    value = "$38.50",
                    subValue = "2 TP hits",
                    accentColor = ElectricCyan,
                    icon = {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = "Signals",
                            tint = ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "metric_signals"
                )
            }
        }

        // Musk First Principles Daily Direct Rule
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard)
                    .border(1.dp, TeslaCyberBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable { onNavigate(AppScreen.FIRST_PRINCIPLES) }
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(TeslaCyberBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MUSK'S FIRST PRINCIPLES FILTER",
                                style = MaterialTheme.typography.labelSmall,
                                color = TeslaCyberBlue,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Tap to Learn →",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Don't reason by analogy. Boil the startup down to the most fundamental physics: What is the simplest path to \$100 today?\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Formula: 2 sales of $50 or 20 pips in EUR/USD. Cut everything else.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonProfitGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Live Market Volatility Telemetry Preview Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    .clickable { onNavigate(AppScreen.MARKET_RADAR) }
                    .padding(14.dp)
                    .testTag("dashboard_telemetry_banner")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(TeslaCyberBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoGraph,
                                contentDescription = "Telemetry",
                                tint = ElectricCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "LIVE VOLATILITY TELEMETRY",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricCyan,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "VIX ${volatilityOverview.marketVix}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (volatilityOverview.marketVix > 25) SolarGold else NeonProfitGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${volatilityOverview.marketRegime} • ${((volatilityOverview.aggregateOrderFlowBuyRatio) * 100).toInt()}% Buy Order Depth",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                        }
                    }

                    Text(
                        text = "Radar →",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonProfitGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Live Transactions Feed
        item {
            SectionHeader(
                title = "Today's Cash Ledger",
                badgeText = "${todayRecords.size} ENTRIES"
            )
        }

        if (todayRecords.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "No Entries",
                            tint = TextTertiary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No cashflow recorded yet today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Execute a signal or log a sale to begin pacing!",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary
                        )
                    }
                }
            }
        } else {
            items(todayRecords, key = { it.id }) { record ->
                RevenueRecordRow(
                    record = record,
                    onDelete = { viewModel.deleteRevenue(record.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Modal Bottom Sheet for Logging Revenue
    if (isQuickAddOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setQuickAddDialogOpen(false) },
            sheetState = sheetState,
            containerColor = SurfaceDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Record Cash Inflow",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Add verified revenue from Stripe, Gumroad, or Signal Trading",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("Source Title (e.g. Client Monthly Retainer)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_revenue_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonProfitGreen,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = newAmount,
                    onValueChange = { newAmount = it },
                    label = { Text("Amount ($ USD)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_revenue_amount"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonProfitGreen,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("SAAS", "SIGNALS", "PRODUCTS", "AGENCY").forEach { cat ->
                        val isSel = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) NeonProfitGreen.copy(alpha = 0.2f) else SurfaceCard)
                                .border(
                                    1.dp,
                                    if (isSel) NeonProfitGreen else BorderSubtle,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) NeonProfitGreen else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val amt = newAmount.toDoubleOrNull() ?: 0.0
                        if (newTitle.isNotBlank() && amt > 0.0) {
                            viewModel.logRevenue(
                                title = newTitle,
                                amount = amt,
                                category = selectedCategory
                            )
                            newTitle = ""
                            newAmount = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_revenue_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonProfitGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Confirm & Update Run-Rate",
                        color = SpaceBlack,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun RevenueRecordRow(
    record: RevenueRecordEntity,
    onDelete: () -> Unit
) {
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(record.timestamp) { timeFormat.format(Date(record.timestamp)) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NeonProfitGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Money",
                        tint = NeonProfitGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = record.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${record.category} • $formattedTime",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "+$${String.format("%.2f", record.amount)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NeonProfitGreen
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = TextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
