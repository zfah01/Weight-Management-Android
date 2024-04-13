package com.example.weathertriggerapp2

import android.app.Application
import com.example.weathertriggerapp2.data.AppContainer
import com.example.weathertriggerapp2.data.AppDataContainer

class CalorieApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}