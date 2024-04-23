package com.example.weathertriggerapp2.notificationHandler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weathertriggerapp2.broadcast.Notification
import com.example.weathertriggerapp2.repository.CalorieCountRepository
import java.util.Calendar

class DistanceByAfternoon(val context: Context) {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAfternoonNotification() {
        val intent = Intent(context, Notification::class.java).apply {
            action = Notification.ACTION_ALARM_7
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            7,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 15)
        calendar.set(Calendar.MINUTE, 0)

        val time = calendar.timeInMillis

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }
}