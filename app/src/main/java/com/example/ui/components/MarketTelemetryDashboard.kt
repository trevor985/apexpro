package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.telemetry.AssetTypeFilter
import com.example.data.telemetry.MarketAssetTelemetry
import com.example.data.telemetry.MarketVolatilityOverview
import com.example.data.telemetry.VolatilityLevel
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

@Composable
fun MarketTelemetryDashboard(
    overview: MarketVolatilityOverview,
    onTriggerShock: () -> Unit,
    onToggleHighFrequency: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(AssetTypeFilter.ALL) }
    var selectedAssetSymbol by remember { mutableStateOf<String?>("BTC/USDT") }

    val filteredAssets = remember(overview.assets, selectedFilter) {
        if (selectedFilter == AssetTypeFilter.ALL) overview.assets
        else overview.assets.filter { it.type == selectedFilter }
    }

    val activeAsset = remember(overview.assets, selectedAssetSymbol) {
        overview.assets.find { it.symbol == selectedAssetSymbol } ?: overview.assets.firstOrNull()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .border(1.dp, TeslaCyberBlue.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header: Title, VIX Gauge, High-Frequency Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (overview.highFrequencyMode) NeonProfitGreen else ElectricCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MARKET TELEMETRY DASHBOARD",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Volatility & Liquidity Radar",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // High Frequency / Tick Rate pill button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (overview.highFrequencyMode) NeonProfitGreen.copy(alpha = 0.15f) else Color(0xFF1E2D4A))
                    .border(
                        1.dp,
                        if (overview.highFrequencyMode) NeonProfitGreen else BorderSubtle,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onToggleHighFrequency() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("toggle_hf_mode_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Speed",
                        tint = if (overview.highFrequencyMode) NeonProfitGreen else TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (overview.highFrequencyMode) "400ms HF" else "1.1s TICK",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (overview.highFrequencyMode) NeonProfitGreen else TextSecondary
                    )
                }
            }
        }

        // VIX Telemetry & Regime Banner
        VixTelemetryBanner(
            vix = overview.marketVix,
            vixChangePercent = overview.vixChangePercent,
            marketRegime = overview.marketRegime,
            onTriggerShock = onTriggerShock
        )

        // Active Asset Volatility Waveform & Envelope
        if (activeAsset != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeAsset.symbol,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${activeAsset.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Text(
                            text = "$${activeAsset.currentPrice} (${if (activeAsset.changePercent >= 0) "+" else ""}${activeAsset.changePercent}%)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (activeAsset.changePercent >= 0) NeonProfitGreen else CrimsonLoss
                        )
                    }

                    // Volatility Level Pill
                    VolatilityBadge(level = activeAsset.volatilityLevel, score = activeAsset.volatilityScore)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Canvas Waveform with Bollinger Bands
                VolatilityWaveformCanvas(
                    priceHistory = activeAsset.priceHistory,
                    upperBand = activeAsset.bollingerUpper,
                    lowerBand = activeAsset.bollingerLower,
                    tickDirection = activeAsset.lastTickDirection,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Telemetry Metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TelemetryMiniStat("ATR (VOL RANGE)", "$${activeAsset.atr}", ElectricCyan)
                    TelemetryMiniStat("VOL SCORE", "${activeAsset.volatilityScore}%", SolarGold)
                    TelemetryMiniStat("ORDER IMBALANCE", "${if (activeAsset.orderImbalancePercent >= 0) "+" else ""}${activeAsset.orderImbalancePercent}%", if (activeAsset.orderImbalancePercent >= 0) NeonProfitGreen else CrimsonLoss)
                }
            }
        }

        // Live Order Flow & Liquidity Depth Bar
        OrderFlowDepthBar(buyRatio = overview.aggregateOrderFlowBuyRatio)

        // Asset Type Filter Tabs
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(AssetTypeFilter.values()) { filter ->
                val isSel = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSel) TeslaCyberBlue.copy(alpha = 0.2f) else Color(0xFF0F172A))
                        .border(
                            1.dp,
                            if (isSel) TeslaCyberBlue else BorderSubtle,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = filter.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) TextPrimary else TextSecondary
                    )
                }
            }
        }

        // Multi-Asset Volatility Matrix Grid
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            filteredAssets.forEach { asset ->
                val isSelected = activeAsset?.symbol == asset.symbol
                AssetVolatilityRow(
                    asset = asset,
                    isSelected = isSelected,
                    onClick = { selectedAssetSymbol = asset.symbol }
                )
            }
        }
    }
}

@Composable
fun VixTelemetryBanner(
    vix: Double,
    vixChangePercent: Double,
    marketRegime: String,
    onTriggerShock: () -> Unit
) {
    val isSpike = vix > 30.0 || marketRegime.contains("TURBULENCE")
    val vixColor = if (isSpike) CrimsonLoss else if (vix > 20.0) SolarGold else NeonProfitGreen

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F172A))
            .border(
                1.5.dp,
                if (isSpike) CrimsonLoss.copy(alpha = 0.8f) else BorderSubtle,
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "VIX VOLATILITY INDEX: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "$vix",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = vixColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${if (vixChangePercent >= 0) "+" else ""}${vixChangePercent}%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (vixChangePercent >= 0) CrimsonLoss else NeonProfitGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(vixColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = marketRegime,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = vixColor,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            // Vol Shock Trigger Button
            Button(
                onClick = onTriggerShock,
                colors = ButtonDefaults.buttonColors(containerColor = if (isSpike) CrimsonLoss else SolarGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("trigger_volatility_shock_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Shock",
                    tint = SpaceBlack,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Shock Sim",
                    color = SpaceBlack,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun VolatilityWaveformCanvas(
    priceHistory: List<Double>,
    upperBand: Double,
    lowerBand: Double,
    tickDirection: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Canvas(modifier = modifier) {
        if (priceHistory.size < 2) return@Canvas

        val w = size.width
        val h = size.height

        val minPrice = minOf(priceHistory.minOrNull() ?: 0.0, lowerBand)
        val maxPrice = maxOf(priceHistory.maxOrNull() ?: 1.0, upperBand)
        val range = (maxPrice - minPrice).coerceAtLeast(0.001)

        fun priceToY(p: Double): Float = (h - ((p - minPrice) / range * (h * 0.8f)) - (h * 0.1f)).toFloat()

        // Background Horizontal Grids
        val gridLines = 3
        for (i in 1..gridLines) {
            val y = h * (i.toFloat() / (gridLines + 1))
            drawLine(
                color = Color(0xFF1E2D4A).copy(alpha = 0.4f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
        }

        // Bollinger Bands Shaded Corridor
        val upperY = priceToY(upperBand)
        val lowerY = priceToY(lowerBand)
        drawRect(
            brush = Brush.verticalGradient(
                listOf(
                    TeslaCyberBlue.copy(alpha = 0.12f),
                    ElectricCyan.copy(alpha = 0.05f)
                )
            ),
            topLeft = Offset(0f, upperY),
            size = Size(w, (lowerY - upperY).coerceAtLeast(2f))
        )

        // Draw upper and lower envelope lines
        drawLine(
            color = TeslaCyberBlue.copy(alpha = 0.4f),
            start = Offset(0f, upperY),
            end = Offset(w, upperY),
            strokeWidth = 1.5f,
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
        )
        drawLine(
            color = TeslaCyberBlue.copy(alpha = 0.4f),
            start = Offset(0f, lowerY),
            end = Offset(w, lowerY),
            strokeWidth = 1.5f,
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
        )

        // Draw Waveform Path
        val stepX = w / (priceHistory.size - 1)
        val path = Path()
        val fillPath = Path()

        val firstY = priceToY(priceHistory.first())
        path.moveTo(0f, firstY)
        fillPath.moveTo(0f, h)
        fillPath.lineTo(0f, firstY)

        var lastPoint = Offset(0f, firstY)

        priceHistory.forEachIndexed { idx, price ->
            val px = idx * stepX
            val py = priceToY(price)
            path.lineTo(px, py)
            fillPath.lineTo(px, py)
            lastPoint = Offset(px, py)
        }

        fillPath.lineTo(w, h)
        fillPath.close()

        // Gradient Fill underneath trajectory
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                listOf(
                    ElectricCyan.copy(alpha = 0.25f),
                    Color.Transparent
                )
            )
        )

        // Trajectory Line
        drawPath(
            path = path,
            color = ElectricCyan,
            style = Stroke(width = 3f, cap = StrokeCap.Round)
        )

        // Real-time pulsing tick beacon at latest point
        val beaconColor = if (tickDirection >= 0) NeonProfitGreen else CrimsonLoss
        drawCircle(
            color = beaconColor.copy(alpha = pulseAlpha * 0.4f),
            radius = 12f,
            center = lastPoint
        )
        drawCircle(
            color = beaconColor,
            radius = 4.5f,
            center = lastPoint
        )
    }
}

@Composable
fun OrderFlowDepthBar(
    buyRatio: Double,
    modifier: Modifier = Modifier
) {
    val buyPercent = (buyRatio * 100).toInt()
    val sellPercent = 100 - buyPercent

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ORDER FLOW DEPTH",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
            }
            Text(
                text = "${buyPercent}% BID / ${sellPercent}% ASK",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (buyPercent >= 50) NeonProfitGreen else CrimsonLoss
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Split Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .weight(buyRatio.toFloat().coerceIn(0.05f, 0.95f))
                    .height(8.dp)
                    .background(NeonProfitGreen)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .weight((1f - buyRatio.toFloat()).coerceIn(0.05f, 0.95f))
                    .height(8.dp)
                    .background(CrimsonLoss)
            )
        }
    }
}

@Composable
fun AssetVolatilityRow(
    asset: MarketAssetTelemetry,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) NeonProfitGreen else BorderSubtle
    val bgColor = if (isSelected) SurfaceCard else Color(0xFF0F172A)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("asset_row_${asset.symbol}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Symbol & Name
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1.2f)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E2D4A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = asset.symbol.take(2),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = asset.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = asset.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                        maxLines = 1
                    )
                }
            }

            // Mini Sparkline
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(30.dp)
                    .padding(horizontal = 6.dp)
            ) {
                SparklineMiniCanvas(
                    points = asset.priceHistory,
                    isPositive = asset.changePercent >= 0
                )
            }

            // Price & Volatility badge
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "$${asset.currentPrice}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${if (asset.changePercent >= 0) "+" else ""}${asset.changePercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (asset.changePercent >= 0) NeonProfitGreen else CrimsonLoss,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (asset.volatilityLevel) {
                                    VolatilityLevel.EXTREME -> CrimsonLoss.copy(alpha = 0.2f)
                                    VolatilityLevel.HIGH -> SolarGold.copy(alpha = 0.2f)
                                    VolatilityLevel.MODERATE -> ElectricCyan.copy(alpha = 0.2f)
                                    VolatilityLevel.LOW -> NeonProfitGreen.copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${asset.volatilityScore.toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (asset.volatilityLevel) {
                                VolatilityLevel.EXTREME -> CrimsonLoss
                                VolatilityLevel.HIGH -> SolarGold
                                VolatilityLevel.MODERATE -> ElectricCyan
                                VolatilityLevel.LOW -> NeonProfitGreen
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SparklineMiniCanvas(
    points: List<Double>,
    isPositive: Boolean,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val lineColor = if (isPositive) NeonProfitGreen else CrimsonLoss

    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas

        val w = size.width
        val h = size.height
        val minP = points.minOrNull() ?: 0.0
        val maxP = points.maxOrNull() ?: 1.0
        val range = (maxP - minP).coerceAtLeast(0.001)

        val stepX = w / (points.size - 1)
        val path = Path()

        points.forEachIndexed { idx, p ->
            val x = idx * stepX
            val y = (h - ((p - minP) / range * h)).toFloat()
            if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun VolatilityBadge(
    level: VolatilityLevel,
    score: Double
) {
    val badgeColor = when (level) {
        VolatilityLevel.EXTREME -> CrimsonLoss
        VolatilityLevel.HIGH -> SolarGold
        VolatilityLevel.MODERATE -> ElectricCyan
        VolatilityLevel.LOW -> NeonProfitGreen
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(badgeColor.copy(alpha = 0.15f))
            .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${level.label} • ${score.toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = badgeColor,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun TelemetryMiniStat(
    label: String,
    value: String,
    color: Color
) {
    Column {
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
