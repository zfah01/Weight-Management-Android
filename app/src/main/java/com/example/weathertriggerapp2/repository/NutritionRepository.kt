package com.example.weathertriggerapp2.repository

import com.example.weathertriggerapp2.data.NutritionResponse
import com.example.weathertriggerapp2.network.NutritionApiService

/**
 * Interface representing Nutrition Repository
 * */
interface NutritionRepository {
    suspend fun getNutritionInfo(query : String, header : String) : List<NutritionResponse>
}

class NetworkNutritionRepository(private val apiService: NutritionApiService) : NutritionRepository {
    override suspend fun getNutritionInfo(
        query : String,
        header : String
    ): List<NutritionResponse> {
        return apiService.getNutritionInfo(header, query).execute().body()!!
    }
}