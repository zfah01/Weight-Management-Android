package com.example.weathertriggerapp2.network

import com.example.weathertriggerapp2.data.NutritionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query 

/**
 * Interface representing Nutrition API http calls
 * */
interface NutritionApiService {
    @Headers("Cache-Control: max-age=86400") // 1 Day - might change
    @GET("nutrition")
    fun getNutritionInfo(
        @Header("x-api-key") apiKey: String,
        @Query("query") query: String
    ): Call<List<NutritionResponse>>
}

