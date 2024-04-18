package com.example.weathertriggerapp2.network

import android.content.Context
import com.example.weathertriggerapp2.repository.NetworkWeatherRepository
import com.example.weathertriggerapp2.repository.WeatherRepository
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// https://blog.stackademic.com/cache-me-if-you-can-achieving-caching-with-retrofit-in-kotlin-e77b38a26417
/**
 * Class for building and caching retrofit http calls and responses
 * */
class WeatherApi(val context: Context) {
    private val baseURL = "https://api.openweathermap.org/"
    private val cacheSize = (5 * 1024 * 1024).toLong()
    private val cache = Cache(context.cacheDir, cacheSize)

    private val client = OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(cachingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseURL)
        .client(client)
        .build()

    private val retrofitService: OpenWeatherApiService by lazy {
        retrofit.create(OpenWeatherApiService::class.java)
    }

    val weatherRepository: WeatherRepository by lazy {
        NetworkWeatherRepository(retrofitService)
    }
}