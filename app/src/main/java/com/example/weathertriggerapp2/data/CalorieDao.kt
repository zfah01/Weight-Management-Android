package com.example.weathertriggerapp2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CalorieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(calorie: Calorie)

    @Delete
    fun delete(calorie: Calorie)

    @Query("SELECT SUM(step_count) FROM calories WHERE number_of_week = :weekNum")
    fun getStepsCountWeekly(weekNum: Int): Double

    @Query("SELECT SUM(calorie_count) FROM calories WHERE number_of_week = :weekNum")
    fun getCaloriesCountWeekly(weekNum: Int): Double

    @Query("SELECT COUNT(*) FROM calories WHERE number_of_week = :weekNum")
    fun getDaysRecorded (weekNum: Int): Int

    @Query("SELECT SUM(sugar_intake) FROM calories WHERE number_of_week = :weekNum")
    fun getSugarCountWeekly(weekNum: Int) : Double

    @Query("SELECT SUM(sat_fat_intake) FROM calories WHERE number_of_week = :weekNum")
    fun getFatCountWeekly(weekNum: Int) : Double
}
