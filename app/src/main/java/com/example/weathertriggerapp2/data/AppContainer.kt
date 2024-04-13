package com.example.weathertriggerapp2.data

import android.content.Context
import com.example.weathertriggerapp2.repository.CalorieRepository
import com.example.weathertriggerapp2.repository.OfflineCaloriesRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val calorieRepository : CalorieRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val calorieRepository : CalorieRepository by lazy {
        OfflineCaloriesRepository(CalorieDatabase.getDatabase(context).calorieDao())
    }
}