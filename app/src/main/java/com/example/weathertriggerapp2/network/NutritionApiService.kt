package com.example.weathertriggerapp2.network

import com.example.weathertriggerapp2.data.NutritionResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// https://api-ninjas.com/api/nutrition
private const val BASE_URL = "https://api.api-ninjas.com/v1/"
interface NutritionApiService {
    @GET("nutrition")
    fun getNutritionInfo(
        @Header("x-api-key") apiKey: String,
        @Query("query") query: String
    ): Call<List<NutritionResponse>>
}

// Still to implement caching? Maybe?
object NutritionApi {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val retrofitService : NutritionApiService by lazy {
        retrofit.create(NutritionApiService::class.java)
    }
}