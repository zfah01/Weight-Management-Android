package com.example.weathertriggerapp2.network

import android.content.Context
import com.example.weathertriggerapp2.repository.NetworkNutritionRepository
import com.example.weathertriggerapp2.repository.NutritionRepository
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// https://blog.stackademic.com/cache-me-if-you-can-achieving-caching-with-retrofit-in-kotlin-e77b38a26417

/**
 * Class for building and caching retrofit http calls and responses
 * */
class NutritionApi(val context: Context) {
    private val baseURL = "https://api.api-ninjas.com/v1/"

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

    private val retrofitService: NutritionApiService by lazy {
        retrofit.create(NutritionApiService::class.java)
    }

    val nutritionRepository: NutritionRepository by lazy {
        NetworkNutritionRepository(retrofitService)
    }
}