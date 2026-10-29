package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.data.model.MarketCandle
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CrimsonLoss
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonProfitGreen
import com.example.ui.theme.SurfaceCard

@Composable
fun CandleStickRadarChart(
    candles: List<MarketCandle>,
    targetPrice: Double? = null,
    stopLossPrice: Double? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .padding(8.dp)
    ) {
        if (candles.isEmpty()) return@Box

        Canvas(modifier = Modifier.matchParentSize()) {
            val minP = candles.minOfOrNull { it.low } ?: 1.0
            val maxP = candles.maxOfOrNull { it.high } ?: 1.0
            val priceRange = (maxP - minP).coerceAtLeast(0.0001)

            val width = size.width
            val height = size.height

            // Grid lines
            val gridColor = Color(0xFF1E2D4A).copy(alpha = 0.5f)
            val numGridLines = 3
            for (i in 1..numGridLines) {
                val y = height * (i.toFloat() / (numGridLines + 1))
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
            }

            // Draw TP dashed line if present
            if (targetPrice != null && targetPrice in minP..maxP) {
                val tpY = (height - ((targetPrice - minP) / priceRange * height)).toFloat()
                drawLine(
                    color = NeonProfitGreen.copy(alpha = 0.8f),
                    start = Offset(0f, tpY),
                    end = Offset(width, tpY),
                    strokeWidth = 2f,
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }

            // Draw SL dashed line if present
            if (stopLossPrice != null && stopLossPrice in minP..maxP) {
                val slY = (height - ((stopLossPrice - minP) / priceRange * height)).toFloat()
                drawLine(
                    color = CrimsonLoss.copy(alpha = 0.8f),
                    start = Offset(0f, slY),
                    end = Offset(width, slY),
                    strokeWidth = 2f,
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }

            val candleCount = candles.size
            val candleSlotWidth = width / candleCount
            val candleBodyWidth = (candleSlotWidth * 0.65f).coerceAtLeast(4f)

            val emaPoints = mutableListOf<Offset>()

            candles.forEachIndexed { index, candle ->
                val xCenter = (index + 0.5f) * candleSlotWidth
                val isBullish = candle.close >= candle.open
                val candleColor = if (isBullish) NeonProfitGreen else CrimsonLoss

                val highY = (height - ((candle.high - minP) / priceRange * height)).toFloat()
                val lowY = (height - ((candle.low - minP) / priceRange * height)).toFloat()
                val openY = (height - ((candle.open - minP) / priceRange * height)).toFloat()
                val closeY = (height - ((candle.close - minP) / priceRange * height)).toFloat()

                // Wick
                drawLine(
                    color = candleColor,
                    start = Offset(xCenter, highY),
                    end = Offset(xCenter, lowY),
                    strokeWidth = 2f
                )

                // Body
                val bodyTop = minOf(openY, closeY)
                val bodyBottom = maxOf(openY, closeY)
                val bodyHeight = (bodyBottom - bodyTop).coerceAtLeast(3f)

                drawRect(
                    color = candleColor,
                    topLeft = Offset(xCenter - (candleBodyWidth / 2f), bodyTop),
                    size = Size(candleBodyWidth, bodyHeight)
                )

                // Accumulate EMA line point
                val midPrice = (candle.open + candle.close) / 2.0
                val midY = (height - ((midPrice - minP) / priceRange * height)).toFloat()
                emaPoints.add(Offset(xCenter, midY))
            }

            // Smooth Trend Line (Cyber Cyan)
            if (emaPoints.size > 1) {
                val path = Path()
                path.moveTo(emaPoints.first().x, emaPoints.first().y)
                for (p in emaPoints.drop(1)) {
                    path.lineTo(p.x, p.y)
                }
                drawPath(
                    path = path,
                    color = ElectricCyan.copy(alpha = 0.7f),
                    style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                )
            }
        }
    }
}
