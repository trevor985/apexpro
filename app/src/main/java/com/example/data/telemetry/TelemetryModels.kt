package com.example.data.telemetry

enum class VolatilityLevel(val label: String, val threshold: Double) {
    LOW("LOW VOL", 25.0),
    MODERATE("MODERATE", 50.0),
    HIGH("HIGH VOL", 75.0),
    EXTREME("EXTREME TURBULENCE", 100.0)
}

enum class AssetTypeFilter(val displayName: String) {
    ALL("All Assets"),
    STOCKS("US Stocks"),
    CRYPTO("Crypto Assets"),
    INDICES("Macro Indices")
}

data class MarketAssetTelemetry(
    val symbol: String,
    val name: String,
    val type: AssetTypeFilter,
    val currentPrice: Double,
    val openPrice: Double,
    val changePercent: Double,
    val volatilityScore: Double, // 0 - 100
    val volatilityLevel: VolatilityLevel,
    val atr: Double, // Average True Range
    val bidVolume: Double,
    val askVolume: Double,
    val orderImbalancePercent: Double, // -100 (heavy sell) to +100 (heavy buy)
    val priceHistory: List<Double>,
    val bollingerUpper: Double,
    val bollingerLower: Double,
    val isSpikeAlert: Boolean = false,
    val lastTickDirection: Int = 0 // +1 for up, -1 for down, 0 neutral
)

data class MarketVolatilityOverview(
    val marketVix: Double,
    val vixChangePercent: Double,
    val marketRegime: String,
    val aggregateOrderFlowBuyRatio: Double, // 0.0 to 1.0 (e.g. 0.62)
    val assets: List<MarketAssetTelemetry>,
    val highFrequencyMode: Boolean = false,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
