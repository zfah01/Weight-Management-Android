package com.example.weathertriggerapp2.notificationHandler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weathertriggerapp2.broadcast.Notification
import java.util.Calendar

// https://www.geeksforgeeks.org/schedule-notifications-in-android/
// https://stackoverflow.com/questions/14980899/how-to-set-time-to-24-hour-format-in-calendar

/**
 * Class for scheduling midday calorie intake notification
 * */
class CalorieMidDayNotification(val context: Context) {
    /**
     * Function for scheduling notification at 3pm
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleMiddayNotification() {
        val intent = Intent(context, Notification::class.java).apply {
            action = Notification.ACTION_ALARM_1
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 15)
        calendar.set(Calendar.MINUTE, 0)

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val time = calendar.timeInMillis

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }
}