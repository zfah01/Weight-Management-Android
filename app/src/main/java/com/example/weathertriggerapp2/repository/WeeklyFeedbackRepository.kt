package com.example.weathertriggerapp2.repository

import android.content.Context
import com.example.weathertriggerapp2.data.CalorieDatabase

class WeeklyFeedbackRepository private constructor(context: Context) {

    private val db: CalorieDatabase = CalorieDatabase.getDatabase(context)

    companion object {
        @Volatile
        private var instance: WeeklyFeedbackRepository? = null

        fun getInstance(context: Context): WeeklyFeedbackRepository {
            return instance ?: synchronized(this) {
                instance ?: WeeklyFeedbackRepository(context).also { instance = it }
            }
        }
    }

    fun getWeeklyCalorieCount(weekNum: Int): Double {
        return db.calorieDao().getCaloriesCountWeekly(weekNum)
    }

    fun getWeeklyStepsCount(weekNum: Int): Double {
        return db.calorieDao().getStepsCountWeekly(weekNum)
    }
}