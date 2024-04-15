package com.example.weathertriggerapp2.notificationHandler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService
import com.example.weathertriggerapp2.broadcast.Notification
import java.util.Calendar
import java.util.Date
import java.util.TimeZone


class WeatherNotification(val context: Context) {
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleWeatherNotification() {
        val intent = Intent(context, Notification::class.java).apply {
            action = Notification.ACTION_ALARM_3
        }

        // Create a PendingIntent for the broadcast
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            3,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Get the selected time and schedule the notification
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
        calendar.add(Calendar.MINUTE, 3) // Schedule notification 1 minute from now

        val time = calendar.timeInMillis


        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }
}