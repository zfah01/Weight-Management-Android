package com.example.weathertriggerapp2.repository

import androidx.annotation.WorkerThread
import com.example.weathertriggerapp2.data.Calorie
import com.example.weathertriggerapp2.data.CalorieDao
import kotlinx.coroutines.flow.Flow

open class CalorieRepository(private val calorieDao: CalorieDao) {
    @WorkerThread
    open suspend fun insert(calorie: Calorie) {
        calorieDao.insert(calorie)
    }
}