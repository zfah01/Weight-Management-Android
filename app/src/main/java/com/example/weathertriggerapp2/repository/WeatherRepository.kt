package com.example.weathertriggerapp2.repository

import com.example.weathertriggerapp2.data.WeatherResponse
import com.example.weathertriggerapp2.network.OpenWeatherApiService

interface WeatherRepository {
    suspend fun getWeatherLocation(appId: String, lat: Double, lon: Double): WeatherResponse
}

class NetworkWeatherRepository(private val apiService: OpenWeatherApiService) : WeatherRepository {
    override suspend fun getWeatherLocation(
        appId: String,
        lat: Double,
        lon: Double
    ): WeatherResponse {
        return apiService.getWeatherLocation(lat, lon, appId).execute().body()!!
    }
}