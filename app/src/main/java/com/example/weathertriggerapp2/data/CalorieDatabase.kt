package com.example.weathertriggerapp2.data

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room

@Database(entities = [Calorie::class], version = 1)
abstract class CalorieDatabase  : RoomDatabase() {
    abstract fun calorieDao(): CalorieDao
    companion object {
        @Volatile
        private var INSTANCE: CalorieDatabase? = null
        fun getDatabase(context: Context): CalorieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalorieDatabase::class.java,
                    "calorie_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

