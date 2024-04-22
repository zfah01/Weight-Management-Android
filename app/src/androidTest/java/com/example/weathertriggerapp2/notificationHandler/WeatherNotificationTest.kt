package com.example.weathertriggerapp2.notificationHandler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith


class WeatherNotificationTest {
    @Test
    fun notificationCorrectlyScheduled() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val weatherNotification = WeatherNotification(context)
    }
}