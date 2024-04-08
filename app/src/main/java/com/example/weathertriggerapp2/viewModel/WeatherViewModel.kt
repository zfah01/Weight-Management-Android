package com.example.weathertriggerapp2.viewModel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.util.Log

import android.app.Application
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weathertriggerapp2.R
import com.example.weathertriggerapp2.data.WeatherResponse
import com.example.weathertriggerapp2.network.WeatherApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.Locale

class WeatherViewModel : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var weatherUiState by mutableStateOf<WeatherResponse?>(null)
        private set


    init {
        getWeatherInfo()
    }

    fun getWeatherInfo() {
            viewModelScope.launch(Dispatchers.IO) {
                Log.d(TAG, "Inside coroutine block...")
                try {
                    // Execute the network request
                    Log.d(TAG, "Making network request...")
                    val response: Response<WeatherResponse> = WeatherApi.retrofitService.getWeather().execute()

                    // Check if the response is successful
                    if (response.isSuccessful) {
                        // If successful, update the weatherUiState with the response body
                        weatherUiState = response.body()
                    } else {
                        Log.e(TAG, "Error occurred")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error occurred: ${e.message}", e)
                }
            }
    }




}