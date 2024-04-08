package com.example.weathertriggerapp2

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.weathertriggerapp2.viewModel.WeatherViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weathertriggerapp2.viewModel.MainScreen


@Composable
fun WeatherApp() {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        val weatherViewModel: WeatherViewModel = viewModel()
        MainScreen(
            weatherUiState = weatherViewModel.weatherUiState
        )

    }
}