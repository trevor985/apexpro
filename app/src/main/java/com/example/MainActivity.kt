package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.ApexNavigationBar
import com.example.ui.screens.BlueprintsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FirstPrinciplesScreen
import com.example.ui.screens.MarketRadarScreen
import com.example.ui.screens.ScaleCalculatorScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SpaceBlack
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.VentureViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: VentureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: VentureViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Handle Android system back press: return to Dashboard if on sub-screens
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        viewModel.navigateTo(AppScreen.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack),
        bottomBar = {
            ApexNavigationBar(
                currentScreen = currentScreen,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SpaceBlack)
                .statusBarsPadding()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
                AppScreen.MARKET_RADAR -> MarketRadarScreen(
                    viewModel = viewModel
                )
                AppScreen.BLUEPRINTS -> BlueprintsScreen(
                    viewModel = viewModel
                )
                AppScreen.FIRST_PRINCIPLES -> FirstPrinciplesScreen(
                    viewModel = viewModel
                )
                AppScreen.SCALE_CALC -> ScaleCalculatorScreen()
            }
        }
    }
}
