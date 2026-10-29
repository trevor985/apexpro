package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.RevenueRecordEntity
import com.example.data.local.SprintTaskEntity
import com.example.data.model.MuskWisdom
import com.example.data.model.TradeSignal
import com.example.data.model.VentureBlueprint
import com.example.data.repository.VentureRepository
import com.example.data.telemetry.MarketVolatilityOverview
import com.example.data.telemetry.MockMarketDataService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    DASHBOARD,
    MARKET_RADAR,
    BLUEPRINTS,
    FIRST_PRINCIPLES,
    SCALE_CALC
}

data class PositionSizeResult(
    val accountBalance: Double,
    val dailyTarget: Double,
    val maxDollarRisk: Double,
    val suggestedLotSize: Double,
    val stopLossPips: Double,
    val riskReward: String
)

class VentureViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VentureRepository
    private val mockMarketDataService = MockMarketDataService(viewModelScope)

    val marketVolatilityOverview: StateFlow<MarketVolatilityOverview> =
        mockMarketDataService.telemetryState

    fun triggerVolatilityShock() {
        mockMarketDataService.triggerVolatilityShock()
    }

    fun toggleTelemetryHighFrequency() {
        mockMarketDataService.toggleHighFrequencyMode()
    }

    init {
        val database = AppDatabase.getDatabase(application)
        repository = VentureRepository(database)
        viewModelScope.launch {
            repository.seedInitialTasksIfEmpty()
            repository.seedInitialRevenueIfEmpty()
        }
    }

    // Current Navigation Screen
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // Daily Revenue Stream
    val todayRevenue: StateFlow<Double> = repository.getTodayTotalRevenue()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 84.50)

    val todayRecords: StateFlow<List<RevenueRecordEntity>> = repository.getTodayRevenueRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sprintTasks: StateFlow<List<SprintTaskEntity>> = repository.getSprintTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Startup Blueprints
    private val _blueprints = MutableStateFlow(repository.getStartupBlueprints())
    val blueprints: StateFlow<List<VentureBlueprint>> = _blueprints.asStateFlow()

    private val _selectedBlueprint = MutableStateFlow<VentureBlueprint?>(null)
    val selectedBlueprint: StateFlow<VentureBlueprint?> = _selectedBlueprint.asStateFlow()

    fun selectBlueprint(blueprint: VentureBlueprint?) {
        _selectedBlueprint.value = blueprint
    }

    fun activateBlueprint(id: String) {
        _blueprints.value = _blueprints.value.map {
            if (it.id == id) it.copy(isActivated = true) else it
        }
    }

    // Live Market Signals
    val liveSignals: StateFlow<List<TradeSignal>> = repository.getLiveMarketSignals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSignal = MutableStateFlow<TradeSignal?>(null)
    val selectedSignal: StateFlow<TradeSignal?> = _selectedSignal.asStateFlow()

    fun selectSignal(signal: TradeSignal?) {
        _selectedSignal.value = signal
    }

    // Musk Wisdom & First Principles
    val muskPrinciples: List<MuskWisdom> = repository.getMuskFirstPrinciples()

    private val _selectedPrinciple = MutableStateFlow(muskPrinciples.first())
    val selectedPrinciple: StateFlow<MuskWisdom> = _selectedPrinciple.asStateFlow()

    fun selectPrinciple(principle: MuskWisdom) {
        _selectedPrinciple.value = principle
    }

    // Quick Add Revenue Dialog
    private val _quickAddDialogOpen = MutableStateFlow(false)
    val quickAddDialogOpen: StateFlow<Boolean> = _quickAddDialogOpen.asStateFlow()

    fun setQuickAddDialogOpen(open: Boolean) {
        _quickAddDialogOpen.value = open
    }

    // Trade Simulation Modal
    private val _tradeSimulationModalOpen = MutableStateFlow(false)
    val tradeSimulationModalOpen: StateFlow<Boolean> = _tradeSimulationModalOpen.asStateFlow()

    fun setTradeSimulationModalOpen(open: Boolean) {
        _tradeSimulationModalOpen.value = open
    }

    // Position Sizer State
    private val _accountBalanceInput = MutableStateFlow(3000.0)
    val accountBalanceInput: StateFlow<Double> = _accountBalanceInput.asStateFlow()

    private val _dailyTargetInput = MutableStateFlow(100.0)
    val dailyTargetInput: StateFlow<Double> = _dailyTargetInput.asStateFlow()

    private val _riskPercentInput = MutableStateFlow(1.5)
    val riskPercentInput: StateFlow<Double> = _riskPercentInput.asStateFlow()

    private val _positionSizeResult = MutableStateFlow<PositionSizeResult?>(null)
    val positionSizeResult: StateFlow<PositionSizeResult?> = _positionSizeResult.asStateFlow()

    init {
        calculatePositionSizing(3000.0, 100.0, 1.5, 25.0)
    }

    fun updatePositionInputs(balance: Double, target: Double, risk: Double) {
        _accountBalanceInput.value = balance
        _dailyTargetInput.value = target
        _riskPercentInput.value = risk
        calculatePositionSizing(balance, target, risk, 25.0)
    }

    private fun calculatePositionSizing(balance: Double, target: Double, risk: Double, targetPips: Double) {
        val res = repository.calculatePositionSize(balance, target, risk, targetPips)
        _positionSizeResult.value = PositionSizeResult(
            accountBalance = res["accountBalance"] as Double,
            dailyTarget = res["dailyTarget"] as Double,
            maxDollarRisk = res["maxDollarRisk"] as Double,
            suggestedLotSize = res["suggestedLotSize"] as Double,
            stopLossPips = res["stopLossPips"] as Double,
            riskReward = res["riskReward"] as String
        )
    }

    fun logRevenue(title: String, amount: Double, category: String, notes: String = "") {
        viewModelScope.launch {
            repository.logRevenue(title, amount, category, notes)
            _quickAddDialogOpen.value = false
        }
    }

    fun deleteRevenue(id: Long) {
        viewModelScope.launch {
            repository.deleteRevenueRecord(id)
        }
    }

    fun toggleTask(id: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(id, completed)
        }
    }

    fun executePaperTrade(signal: TradeSignal, lotSize: Double, profitPips: Double) {
        viewModelScope.launch {
            // Profit = pips * lotSize * 10
            val profitDollars = Math.round((profitPips * lotSize * 10.0) * 100.0) / 100.0
            repository.logRevenue(
                title = "${signal.symbol} ${signal.direction} Scalp TP",
                amount = profitDollars,
                category = "SIGNALS",
                notes = "Executed at ${signal.currentPrice} | +${profitPips} pips on ${lotSize} lot"
            )
            _tradeSimulationModalOpen.value = false
        }
    }
}
