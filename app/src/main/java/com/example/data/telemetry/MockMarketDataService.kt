package com.example.data.telemetry

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class MockMarketDataService(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    private var isHighFrequency = false
    private var isShockActive = false

    private val initialAssets = listOf(
        createAsset("TSLA", "Tesla Inc", AssetTypeFilter.STOCKS, 268.40, 68.5, VolatilityLevel.HIGH),
        createAsset("NVDA", "Nvidia Corp", AssetTypeFilter.STOCKS, 142.10, 74.2, VolatilityLevel.HIGH),
        createAsset("BTC/USDT", "Bitcoin", AssetTypeFilter.CRYPTO, 68420.0, 82.0, VolatilityLevel.EXTREME),
        createAsset("ETH/USDT", "Ethereum", AssetTypeFilter.CRYPTO, 3510.50, 71.0, VolatilityLevel.HIGH),
        createAsset("SOL/USDT", "Solana", AssetTypeFilter.CRYPTO, 184.75, 88.5, VolatilityLevel.EXTREME),
        createAsset("SPY", "S&P 500 ETF", AssetTypeFilter.INDICES, 582.30, 31.0, VolatilityLevel.LOW),
        createAsset("AAPL", "Apple Inc", AssetTypeFilter.STOCKS, 234.90, 28.5, VolatilityLevel.LOW)
    )

    private val _telemetryState = MutableStateFlow(
        MarketVolatilityOverview(
            marketVix = 24.3,
            vixChangePercent = 3.8,
            marketRegime = "EXPANDING VOLATILITY",
            aggregateOrderFlowBuyRatio = 0.58,
            assets = initialAssets,
            highFrequencyMode = false
        )
    )
    val telemetryState: StateFlow<MarketVolatilityOverview> = _telemetryState.asStateFlow()

    init {
        startSimulationLoop()
    }

    private fun startSimulationLoop() {
        scope.launch {
            while (true) {
                val delayTime = if (isHighFrequency) 400L else 1100L
                delay(delayTime)
                tickMarket()
            }
        }
    }

    private fun tickMarket() {
        val current = _telemetryState.value
        val shockMultiplier = if (isShockActive) 4.5 else 1.0

        val updatedAssets = current.assets.map { asset ->
            val volatilityFactor = (asset.volatilityScore / 100.0) * shockMultiplier
            val drift = (Random.nextDouble() - 0.49) * 0.003
            val shockNoise = if (isShockActive) (Random.nextDouble() - 0.5) * 0.035 else 0.0
            val priceChangePercent = drift + (Random.nextDouble() - 0.5) * 0.012 * volatilityFactor + shockNoise

            val newPrice = (asset.currentPrice * (1.0 + priceChangePercent)).coerceAtLeast(0.01)
            val roundedPrice = if (newPrice > 1000) Math.round(newPrice * 100.0) / 100.0
                               else Math.round(newPrice * 1000.0) / 1000.0

            val tickDir = if (roundedPrice > asset.currentPrice) 1 else if (roundedPrice < asset.currentPrice) -1 else 0

            val newHistory = (asset.priceHistory.takeLast(24) + roundedPrice)
            val mean = newHistory.average()
            val stdDev = calculateStdDev(newHistory, mean)

            val bollingerUpper = mean + (2 * stdDev)
            val bollingerLower = mean - (2 * stdDev)

            val dynamicVolScore = (asset.volatilityScore + (Random.nextDouble() - 0.5) * 2.0 * shockMultiplier)
                .coerceIn(10.0, 99.0)

            val level = when {
                dynamicVolScore >= 80.0 -> VolatilityLevel.EXTREME
                dynamicVolScore >= 60.0 -> VolatilityLevel.HIGH
                dynamicVolScore >= 35.0 -> VolatilityLevel.MODERATE
                else -> VolatilityLevel.LOW
            }

            // Order Flow Volume simulation
            val buyFactor = 0.5 + (tickDir * 0.15) + (Random.nextDouble() - 0.5) * 0.2
            val totalVol = Random.nextDouble(100.0, 800.0)
            val bidVol = (totalVol * buyFactor).coerceIn(10.0, totalVol)
            val askVol = totalVol - bidVol
            val imbalance = ((bidVol - askVol) / totalVol) * 100.0

            asset.copy(
                currentPrice = roundedPrice,
                changePercent = Math.round(((roundedPrice - asset.openPrice) / asset.openPrice * 100.0) * 100.0) / 100.0,
                volatilityScore = Math.round(dynamicVolScore * 10.0) / 10.0,
                volatilityLevel = level,
                atr = Math.round((stdDev * 1.5) * 100.0) / 100.0,
                bidVolume = Math.round(bidVol * 10.0) / 10.0,
                askVolume = Math.round(askVol * 10.0) / 10.0,
                orderImbalancePercent = Math.round(imbalance * 10.0) / 10.0,
                priceHistory = newHistory,
                bollingerUpper = Math.round(bollingerUpper * 100.0) / 100.0,
                bollingerLower = Math.round(bollingerLower * 100.0) / 100.0,
                isSpikeAlert = isShockActive || dynamicVolScore > 85.0,
                lastTickDirection = tickDir
            )
        }

        // Aggregate VIX
        val avgVol = updatedAssets.map { it.volatilityScore }.average()
        val newVix = Math.round((avgVol * 0.38) * 10.0) / 10.0
        val totalBids = updatedAssets.sumOf { it.bidVolume }
        val totalAsks = updatedAssets.sumOf { it.askVolume }
        val aggregateBuyRatio = (totalBids / (totalBids + totalAsks).coerceAtLeast(1.0))

        val regime = when {
            isShockActive -> "TURBULENCE SPIKE DETECTED"
            newVix > 30.0 -> "HIGH TURBULENCE REGIME"
            newVix > 20.0 -> "EXPANDING VOLATILITY"
            else -> "STABLE COMPRESSION"
        }

        _telemetryState.value = current.copy(
            marketVix = newVix,
            vixChangePercent = Math.round(((newVix - 23.5) / 23.5 * 100.0) * 10.0) / 10.0,
            marketRegime = regime,
            aggregateOrderFlowBuyRatio = Math.round(aggregateBuyRatio * 100.0) / 100.0,
            assets = updatedAssets,
            highFrequencyMode = isHighFrequency,
            lastUpdatedTimestamp = System.currentTimeMillis()
        )

        // Reset shock after a few cycles
        if (isShockActive && Random.nextInt(5) == 0) {
            isShockActive = false
        }
    }

    fun triggerVolatilityShock() {
        isShockActive = true
        tickMarket()
    }

    fun toggleHighFrequencyMode() {
        isHighFrequency = !isHighFrequency
        _telemetryState.value = _telemetryState.value.copy(highFrequencyMode = isHighFrequency)
    }

    private fun calculateStdDev(values: List<Double>, mean: Double): Double {
        if (values.size < 2) return 0.5
        val sumSquaredDiff = values.sumOf { (it - mean).pow(2) }
        return sqrt(sumSquaredDiff / values.size)
    }

    private fun createAsset(
        symbol: String,
        name: String,
        type: AssetTypeFilter,
        price: Double,
        volScore: Double,
        level: VolatilityLevel
    ): MarketAssetTelemetry {
        val history = mutableListOf<Double>()
        var curr = price * 0.98
        for (i in 0..20) {
            curr += (Random.nextDouble() - 0.48) * (price * 0.005)
            history.add(Math.round(curr * 100.0) / 100.0)
        }
        history.add(price)
        val mean = history.average()
        val stdDev = (price * 0.015)

        return MarketAssetTelemetry(
            symbol = symbol,
            name = name,
            type = type,
            currentPrice = price,
            openPrice = history.first(),
            changePercent = Math.round(((price - history.first()) / history.first() * 100.0) * 100.0) / 100.0,
            volatilityScore = volScore,
            volatilityLevel = level,
            atr = Math.round((price * 0.02) * 100.0) / 100.0,
            bidVolume = 420.0,
            askVolume = 380.0,
            orderImbalancePercent = 10.5,
            priceHistory = history,
            bollingerUpper = price + (2 * stdDev),
            bollingerLower = price - (2 * stdDev),
            isSpikeAlert = false,
            lastTickDirection = 1
        )
    }
}
