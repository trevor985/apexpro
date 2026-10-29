package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.example.data.model.SignalDirection
import com.example.data.model.TradeSignal
import com.example.ui.components.CandleStickRadarChart
import com.example.ui.components.MarketTelemetryDashboard
import com.example.ui.components.LivePulseIndicator
import com.example.ui.components.SectionHeader
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CrimsonLoss
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.NeonProfitGreen
import com.example.ui.theme.SolarGold
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TeslaCyberBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.VentureViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketRadarScreen(
    viewModel: VentureViewModel,
    modifier: Modifier = Modifier
) {
    val signals by viewModel.liveSignals.collectAsState()
    val volatilityOverview by viewModel.marketVolatilityOverview.collectAsState()
    val selectedSignalFromVm by viewModel.selectedSignal.collectAsState()
    val activeSignal = selectedSignalFromVm ?: signals.firstOrNull()

    val positionResult by viewModel.positionSizeResult.collectAsState()
    val isSimulationOpen by viewModel.tradeSimulationModalOpen.collectAsState()

    var activeViewMode by remember { mutableStateOf("TELEMETRY") } // "TELEMETRY" or "SIGNALS"

    var simLotSize by remember { mutableStateOf("0.25") }
    var simPips by remember { mutableStateOf("25.0") }

    var balanceSlider by remember { mutableStateOf(3000f) }
    var riskSlider by remember { mutableStateOf(1.5f) }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MARKET INTELLIGENCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (activeViewMode == "TELEMETRY") "Volatility Telemetry" else "Forex & Asset Signals",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                LivePulseIndicator(label = "TICK FEED LIVE", color = NeonProfitGreen)
            }
        }

        // View Mode Switcher
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeViewMode == "TELEMETRY") TeslaCyberBlue.copy(alpha = 0.25f) else Color.Transparent)
                        .clickable { activeViewMode = "TELEMETRY" }
                        .padding(vertical = 9.dp)
                        .testTag("tab_telemetry_dashboard"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "VOLATILITY TELEMETRY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (activeViewMode == "TELEMETRY") NeonProfitGreen else TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeViewMode == "SIGNALS") TeslaCyberBlue.copy(alpha = 0.25f) else Color.Transparent)
                        .clickable { activeViewMode = "SIGNALS" }
                        .padding(vertical = 9.dp)
                        .testTag("tab_signals_sizing"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SIGNALS & $100 SIZER",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (activeViewMode == "SIGNALS") NeonProfitGreen else TextSecondary
                    )
                }
            }
        }

        if (activeViewMode == "TELEMETRY") {
            item {
                MarketTelemetryDashboard(
                    overview = volatilityOverview,
                    onTriggerShock = { viewModel.triggerVolatilityShock() },
                    onToggleHighFrequency = { viewModel.toggleTelemetryHighFrequency() }
                )
            }
        } else {
            // Horizontal Pair / Asset Carousel
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                items(signals, key = { it.id }) { sig ->
                    val isSelected = activeSignal?.id == sig.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) SurfaceCard else Color(0xFF0F172A))
                            .border(
                                1.5.dp,
                                if (isSelected) NeonProfitGreen else BorderSubtle,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectSignal(sig) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = sig.symbol,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (sig.direction == SignalDirection.BUY) NeonProfitGreen.copy(alpha = 0.2f)
                                            else CrimsonLoss.copy(alpha = 0.2f)
                                        )
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = sig.direction.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (sig.direction == SignalDirection.BUY) NeonProfitGreen else CrimsonLoss,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${sig.currentPrice}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (sig.priceChangePercent >= 0) NeonProfitGreen else CrimsonLoss
                            )
                        }
                    }
                }
            }
        }

        // Active Signal Candlestick Chart & Details
        if (activeSignal != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${activeSignal.assetName} (${activeSignal.timeframe})",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Current Price: ${activeSignal.currentPrice}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonProfitGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${activeSignal.winProbability}% Win Rate",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NeonProfitGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Candlestick Radar Canvas Chart
                    CandleStickRadarChart(
                        candles = activeSignal.recentCandles,
                        targetPrice = activeSignal.takeProfit1,
                        stopLossPrice = activeSignal.stopLoss
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Signal Telemetry Matrix (Entry, SL, TP1, R:R)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SignalParamBox(label = "ENTRY", value = "${activeSignal.entryPrice}", color = ElectricCyan, modifier = Modifier.weight(1f))
                        SignalParamBox(label = "STOP LOSS", value = "${activeSignal.stopLoss}", color = CrimsonLoss, modifier = Modifier.weight(1f))
                        SignalParamBox(label = "TAKE PROFIT", value = "${activeSignal.takeProfit1}", color = NeonProfitGreen, modifier = Modifier.weight(1f))
                        SignalParamBox(label = "R:R RATIO", value = activeSignal.riskRewardRatio, color = SolarGold, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Algorithmic Rationale:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = activeSignal.rationale,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Execute Paper Trade Simulation Button
                    Button(
                        onClick = { viewModel.setTradeSimulationModalOpen(true) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("execute_signal_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonProfitGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Execute",
                            tint = SpaceBlack
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Execute Paper Trade & Book To Daily Goal",
                            color = SpaceBlack,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Elon's $100/Day Position Sizer & Risk Calculator
        item {
            SectionHeader(title = "Elon's $100/Day Position Sizer", badgeText = "STRICT RISK")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceCard)
                    .border(1.dp, TeslaCyberBlue.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Text(
                    text = "First Principles: How much to risk to make exactly $100/day without blowing up your account.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Trading Account Balance: $${balanceSlider.toInt()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = balanceSlider,
                    onValueChange = {
                        balanceSlider = it
                        viewModel.updatePositionInputs(it.toDouble(), 100.0, riskSlider.toDouble())
                    },
                    valueRange = 500f..10000f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonProfitGreen,
                        activeTrackColor = NeonProfitGreen,
                        inactiveTrackColor = Color(0xFF1E2D4A)
                    ),
                    modifier = Modifier.testTag("slider_balance")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Max Risk Per Trade: ${String.format("%.1f", riskSlider)}% ($${String.format("%.2f", balanceSlider * (riskSlider / 100.0))})",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = riskSlider,
                    onValueChange = {
                        riskSlider = it
                        viewModel.updatePositionInputs(balanceSlider.toDouble(), 100.0, it.toDouble())
                    },
                    valueRange = 0.5f..5.0f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricCyan,
                        activeTrackColor = ElectricCyan,
                        inactiveTrackColor = Color(0xFF1E2D4A)
                    ),
                    modifier = Modifier.testTag("slider_risk")
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (positionResult != null) {
                    val res = positionResult!!
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F172A))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("SUGGESTED LOT", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                Text(
                                    "${res.suggestedLotSize} Lots",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonProfitGreen
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F172A))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("MAX LOSS CAP", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                Text(
                                    "-$${String.format("%.2f", res.maxDollarRisk)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = CrimsonLoss
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Rule: On a 25-pip win, you lock in exactly $100.00 profit while risking only $${String.format("%.2f", res.maxDollarRisk)} (${String.format("%.1f", riskSlider)}%).",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldGlow,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Modal Sheet for Executing Paper Trade
    if (isSimulationOpen && activeSignal != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setTradeSimulationModalOpen(false) },
            sheetState = sheetState,
            containerColor = SurfaceDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Execute Paper Trade: ${activeSignal.symbol}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Simulate a live execution on ${activeSignal.direction} signal at ${activeSignal.currentPrice}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = simLotSize,
                    onValueChange = { simLotSize = it },
                    label = { Text("Position Lot Size (e.g. 0.20 standard)") },
                    modifier = Modifier.fillMaxWidth(),
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
                    value = simPips,
                    onValueChange = { simPips = it },
                    label = { Text("Take Profit Move in Pips (e.g. 25 pips)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonProfitGreen,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                val lot = simLotSize.toDoubleOrNull() ?: 0.20
                val pips = simPips.toDoubleOrNull() ?: 25.0
                val estProfit = Math.round(pips * lot * 10.0 * 100.0) / 100.0

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonProfitGreen.copy(alpha = 0.15f))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estimated Realized Profit:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "+$$estProfit",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = NeonProfitGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        viewModel.executePaperTrade(activeSignal, lot, pips)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_paper_trade_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonProfitGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Close Trade at TP & Book +$$estProfit",
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
fun SignalParamBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F172A))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
                fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
