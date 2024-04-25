package com.example.weathertriggerapp2.calorieValuesHandler

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
 * Class for scheduling calorie insertion into 'calories' table
 * */
class InsertTotalCalories(val context: Context) {

    /**
     * Function for scheduling database insertion at 11.59pm
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleInsertCalorieNotification() {
        val intent = Intent(context, Notification::class.java).apply {
            action = Notification.ACTION_ALARM_4
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            4,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)

        val time = calendar.timeInMillis

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }
}