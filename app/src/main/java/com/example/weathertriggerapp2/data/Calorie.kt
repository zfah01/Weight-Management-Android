package com.example.weathertriggerapp2.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calories")
data class Calorie(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "calorie_count")
    val calorieCount: String?,
    @ColumnInfo(name = "step_count")
    val stepCount: Double,
    @ColumnInfo(name = "sat_fat_intake")
    val saturatedFat: String?,
    @ColumnInfo(name = "sugar_intake")
    val sugar: String?,
    @ColumnInfo(name = "number_of_week")
    val weekNum: Int
)