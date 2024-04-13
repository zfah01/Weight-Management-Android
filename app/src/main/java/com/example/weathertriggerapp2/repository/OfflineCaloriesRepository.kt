package com.example.weathertriggerapp2.repository

import com.example.weathertriggerapp2.data.Calorie
import com.example.weathertriggerapp2.data.CalorieDao

class OfflineCaloriesRepository(private val calorieDao: CalorieDao) : CalorieRepository(calorieDao) {
    override suspend fun insert(calorie: Calorie) = calorieDao.insert(calorie)
}