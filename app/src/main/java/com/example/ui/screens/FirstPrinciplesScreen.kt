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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SprintTaskEntity
import com.example.data.model.MuskWisdom
import com.example.ui.components.LivePulseIndicator
import com.example.ui.components.SectionHeader
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonProfitGreen
import com.example.ui.theme.SolarGold
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TeslaCyberBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.VentureViewModel

@Composable
fun FirstPrinciplesScreen(
    viewModel: VentureViewModel,
    modifier: Modifier = Modifier
) {
    val principles = viewModel.muskPrinciples
    val selectedPrinciple by viewModel.selectedPrinciple.collectAsState()
    val tasks by viewModel.sprintTasks.collectAsState()

    var selectedBottleneck by remember { mutableStateOf("No clients yet") }

    val bottleneckSolutions = remember {
        mapOf(
            "No clients yet" to "Root Physics: You haven't made an offer to 50 specific humans. Do NOT build an ad campaign. Direct message 15 podcasters or businesses with a free pre-made sample of your work today. 3 will reply, 1 will buy.",
            "Can't code complex software" to "Root Physics: Software is just automation of logic. Build an MVP using Google Sheets, Make.com, or Airtable. Charge \$29/month manually via Stripe payment link before writing a single line of backend.",
            "Trading feels risky" to "Root Physics: Risk is mathematical, not emotional. Size your lot size down to 0.10. Put Stop Loss at -15 pips (\$15 risk). If trade hits TP at +25 pips, you gain \$25 with zero account blowup risk.",
            "Building is taking too long" to "Root Physics: Apply Step 2 (Delete the part). Remove 75% of your features. The customer only pays for the output, not the settings screen. Launch what works in 2 hours."
        )
    }

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
                        text = "THE 5-STEP ALGORITHM",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Musk First Principles",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                LivePulseIndicator(label = "SPACEX / TESLA METHOD", color = TeslaCyberBlue)
            }
        }

        // Horizontal Step Selector (Step 1 to Step 5)
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(principles) { p ->
                    val isSel = selectedPrinciple.id == p.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) TeslaCyberBlue.copy(alpha = 0.2f) else SurfaceCard)
                            .border(
                                1.5.dp,
                                if (isSel) TeslaCyberBlue else BorderSubtle,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectPrinciple(p) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(if (isSel) TeslaCyberBlue else Color(0xFF1E2D4A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${p.step}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) SpaceBlack else TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = p.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) TextPrimary else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Active Step Detailed Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceCard)
                    .border(1.dp, TeslaCyberBlue.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TeslaCyberBlue.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "STEP ${selectedPrinciple.step} OF 5",
                            style = MaterialTheme.typography.labelSmall,
                            color = TeslaCyberBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedPrinciple.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = SolarGold,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = selectedPrinciple.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quote Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .padding(12.dp)
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "Quote",
                            tint = TeslaCyberBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedPrinciple.quote,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = TextPrimary,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Tactical Execution for $100/Day:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = selectedPrinciple.tacticalBreakdown,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonProfitGreen.copy(alpha = 0.1f))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "DAILY DIAGNOSTIC QUESTION",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonProfitGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = selectedPrinciple.diagnosticPrompt,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Bottleneck Solver Tool
        item {
            SectionHeader(title = "First Principles Bottleneck Solver", badgeText = "INSTANT FIX")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceCard)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "What is currently stopping you from making $100 today?",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Bottleneck selector chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(bottleneckSolutions.keys.toList()) { key ->
                        val isSel = selectedBottleneck == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) SolarGold.copy(alpha = 0.2f) else Color(0xFF0F172A))
                                .border(
                                    1.dp,
                                    if (isSel) SolarGold else BorderSubtle,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedBottleneck = key }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) SolarGold else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = bottleneckSolutions[selectedBottleneck] ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Today's Execution Sprint Quests
        item {
            SectionHeader(title = "Today's $100 Sprint Quests", badgeText = "${tasks.count { it.isCompleted }}/${tasks.size} DONE")
        }

        items(tasks, key = { it.id }) { task ->
            SprintTaskRow(
                task = task,
                onToggle = { isChecked ->
                    viewModel.toggleTask(task.id, isChecked)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SprintTaskRow(
    task: SprintTaskEntity,
    onToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .clickable { onToggle(!task.isCompleted) }
            .padding(12.dp)
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
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = NeonProfitGreen,
                        uncheckedColor = BorderSubtle,
                        checkmarkColor = SpaceBlack
                    ),
                    modifier = Modifier.testTag("task_checkbox_${task.id}")
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (task.isCompleted) TextTertiary else TextPrimary
                    )
                    Text(
                        text = "${task.category} • Potential Yield: +$${task.potentialYield.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (task.isCompleted) NeonProfitGreen else TextTertiary
                    )
                }
            }
        }
    }
}
