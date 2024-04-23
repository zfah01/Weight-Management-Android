package com.example.weathertriggerapp2.repository

import androidx.annotation.WorkerThread
import com.example.weathertriggerapp2.data.Calorie
import com.example.weathertriggerapp2.data.CalorieDao
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing Calorie Repository
 * */
open class CalorieRepository(private val calorieDao: CalorieDao) {
    @WorkerThread
    open suspend fun insert(calorie: Calorie) {
        calorieDao.insert(calorie)
    }

    open fun getStepsCountWeekly(weekNum: Int): Double {
        return calorieDao.getStepsCountWeekly(weekNum)
    }

    open fun getWeeklyCalorieCount(weekNum: Int): Double {
        return calorieDao.getCaloriesCountWeekly(weekNum)
    }

    open fun getDaysRecorded(weekNum: Int) : Int {
        return calorieDao.getDaysRecorded(weekNum)
    }

    open fun getWeeklySugarCount(weekNum: Int) : Double {
        return calorieDao.getSugarCountWeekly(weekNum)
    }

    open fun getWeeklyFatCount(weekNum: Int) : Double {
        return calorieDao.getFatCountWeekly(weekNum)
    }

    open fun delete(weekNum: Int) {
        return calorieDao.delete(weekNum)
    }
}