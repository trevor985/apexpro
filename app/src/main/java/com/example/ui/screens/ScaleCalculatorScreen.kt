package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LivePulseIndicator
import com.example.ui.components.SectionHeader
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.NeonProfitGreen
import com.example.ui.theme.SolarGold
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TeslaCyberBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun ScaleCalculatorScreen(
    modifier: Modifier = Modifier
) {
    var targetDailySlider by remember { mutableStateOf(100f) }

    val dailyAmount = targetDailySlider.toDouble()
    val monthlyAmount = dailyAmount * 30
    val annualAmount = dailyAmount * 365

    // Unit math
    val digitalSalesNeeded = (dailyAmount / 49.0).coerceAtLeast(1.0)
    val saasSubsNeeded = (monthlyAmount / 29.0).coerceAtLeast(1.0)
    val highTicketClientsNeeded = (monthlyAmount / 1200.0).coerceAtLeast(1.0)
    val forexPipsNeeded = (dailyAmount / 5.0).coerceAtLeast(1.0) // on 0.5 lot

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
                        text = "HYPER-GROWTH SIMULATOR",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Scale $100 to $1,000/Day",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                LivePulseIndicator(label = "COMPOUNDING", color = NeonProfitGreen)
            }
        }

        // Daily Target Slider Box
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceCard)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "SELECT DAILY REVENUE GOAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$${targetDailySlider.toInt()}",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = NeonProfitGreen
                    )
                    Text(
                        text = " / day",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextTertiary,
                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Slider(
                    value = targetDailySlider,
                    onValueChange = { targetDailySlider = it },
                    valueRange = 50f..1000f,
                    steps = 18,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonProfitGreen,
                        activeTrackColor = NeonProfitGreen,
                        inactiveTrackColor = Color(0xFF1E2D4A)
                    ),
                    modifier = Modifier.testTag("slider_daily_target")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("$50/day", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                    Text("$500/day", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                    Text("$1,000/day ($30k/mo)", style = MaterialTheme.typography.labelSmall, color = NeonProfitGreen)
                }
            }
        }

        // Compounding Projection Cards
        item {
            SectionHeader(title = "Cumulative Cashflow Projection", badgeText = "COMPOUND ENGINE")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProjectionCard(
                    title = "30 DAYS",
                    amount = "$${String.format("%,.0f", monthlyAmount)}",
                    detail = "Monthly Run-Rate",
                    color = ElectricCyan,
                    modifier = Modifier.weight(1f)
                )
                ProjectionCard(
                    title = "90 DAYS",
                    amount = "$${String.format("%,.0f", monthlyAmount * 3)}",
                    detail = "Quarterly Yield",
                    color = SolarGold,
                    modifier = Modifier.weight(1f)
                )
                ProjectionCard(
                    title = "1 YEAR",
                    amount = "$${String.format("%,.0f", annualAmount)}",
                    detail = "Annualized Cash",
                    color = NeonProfitGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // What You Need In The Real World Matrix
        item {
            SectionHeader(title = "Exact Math To Hit This Today", badgeText = "PHYSICS OF REVENUE")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceCard)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                UnitMathRow(
                    label = "Digital Prompt / Template Sales",
                    formula = "${String.format("%.1f", digitalSalesNeeded)} purchases / day",
                    pricePoint = "@ $49 each (Gumroad / Stripe)",
                    color = NeonProfitGreen
                )

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderSubtle))

                UnitMathRow(
                    label = "Micro-SaaS Recurring Users",
                    formula = "${saasSubsNeeded.toInt()} active subscribers",
                    pricePoint = "@ $29 / month recurring",
                    color = ElectricCyan
                )

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderSubtle))

                UnitMathRow(
                    label = "High-Ticket Automation Clients",
                    formula = "${String.format("%.1f", highTicketClientsNeeded)} active clients",
                    pricePoint = "@ $1,200 / month retainer",
                    color = SolarGold
                )

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderSubtle))

                UnitMathRow(
                    label = "Forex / Scalping Execution",
                    formula = "${forexPipsNeeded.toInt()} pips / day",
                    pricePoint = "Using 0.50 standard lot size",
                    color = TeslaCyberBlue
                )
            }
        }

        // Elon's Law of Extreme Scale
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF131D31),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .border(1.dp, TeslaCyberBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = "Bolt",
                            tint = SolarGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ELON'S LAW OF INFINITE LEVERAGE",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Labor and time do not scale. Code, algorithms, and media scale infinitely. If your startup requires you to trade hours for dollars, you built a job, not a startup. Build the machine once, then let it print \$100 while you sleep.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ProjectionCard(
    title: String,
    amount: String,
    detail: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
                fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = detail,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun UnitMathRow(
    label: String,
    formula: String,
    pricePoint: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = pricePoint,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
        Text(
            text = formula,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
