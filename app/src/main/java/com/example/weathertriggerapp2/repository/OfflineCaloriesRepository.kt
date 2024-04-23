package com.example.weathertriggerapp2.repository

import com.example.weathertriggerapp2.data.Calorie
import com.example.weathertriggerapp2.data.CalorieDao

class OfflineCaloriesRepository(private val calorieDao: CalorieDao) : CalorieRepository(calorieDao) {
    override suspend fun insert(calorie: Calorie) = calorieDao.insert(calorie)
    override fun getStepsCountWeekly(weekNum: Int): Double = calorieDao.getStepsCountWeekly(weekNum)
    override fun getWeeklyCalorieCount(weekNum: Int): Double = calorieDao.getCaloriesCountWeekly(weekNum)
    override fun getDaysRecorded(weekNum: Int): Int = calorieDao.getDaysRecorded(weekNum)
    override fun getWeeklySugarCount(weekNum: Int): Double = calorieDao.getSugarCountWeekly(weekNum)
    override fun getWeeklyFatCount(weekNum: Int): Double = calorieDao.getFatCountWeekly(weekNum)
    override fun delete(weekNum: Int) = calorieDao.delete(weekNum)

}