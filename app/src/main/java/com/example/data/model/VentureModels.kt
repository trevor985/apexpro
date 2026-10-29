package com.example.data.model

data class VentureBlueprint(
    val id: String,
    val title: String,
    val tag: String,
    val category: String, // "AI Micro-SaaS", "Algorithmic Trading", "Digital Arbitrage", "B2B Automation"
    val targetDailyYield: Double, // e.g. 100.0, 120.0, 150.0
    val targetMonthlyYield: Double, // e.g. 3000.0, 3600.0
    val formulaSummary: String, // "2 sales/day @ $50" or "20 clients @ $150/mo"
    val muskPrincipleAngle: String,
    val description: String,
    val unitEconomics: List<UnitEconomicItem>,
    val launchChecklist: List<String>,
    val techStack: List<String>,
    val acquisitionScript: String,
    val isActivated: Boolean = false
)

data class UnitEconomicItem(
    val label: String,
    val value: String,
    val detail: String
)

data class MarketCandle(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
)

enum class SignalDirection {
    BUY, SELL
}

data class TradeSignal(
    val id: String,
    val symbol: String, // e.g. "EUR/USD", "XAU/USD", "BTC/USDT"
    val assetName: String,
    val assetType: String, // "Forex", "Commodity", "Crypto"
    val currentPrice: Double,
    val priceChangePercent: Double,
    val direction: SignalDirection,
    val entryPrice: Double,
    val stopLoss: Double,
    val takeProfit1: Double,
    val takeProfit2: Double,
    val winProbability: Int, // e.g. 84
    val riskRewardRatio: String, // "1:2.8"
    val timeframe: String, // "15M", "1H"
    val rationale: String,
    val recentCandles: List<MarketCandle>
)

data class MuskWisdom(
    val id: String,
    val step: Int,
    val title: String,
    val subtitle: String,
    val quote: String,
    val tacticalBreakdown: String,
    val diagnosticPrompt: String
)

data class DailyEngineTelemetry(
    val targetDaily: Double = 100.0,
    val currentDailyEarned: Double,
    val monthlyPacing: Double,
    val activeVentureEngines: Int,
    val winRatePercent: Int,
    val streakDays: Int
)
