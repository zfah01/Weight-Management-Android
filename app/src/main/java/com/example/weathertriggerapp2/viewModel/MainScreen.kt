package com.example.weathertriggerapp2.viewModel

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.weathertriggerapp2.data.WeatherResponse
import com.google.gson.Gson

@Composable
fun MainScreen(
    weatherUiState: WeatherResponse?,
    modifier: Modifier = Modifier
) {
    ResultScreen(weatherUiState, modifier)
}

/**
 * ResultScreen displaying number of photos retrieved.
 */
@Composable
fun ResultScreen(weather: WeatherResponse?, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        JsonInfo(weather)
    }
}

@Composable
fun JsonInfo(weather: WeatherResponse?) {
    val gson = Gson()
    val jsonString = weather?.let { gson.toJson(it) } ?: "No weather information available"

    Text(text = jsonString)
}
