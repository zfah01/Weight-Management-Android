package com.example.weathertriggerapp2.notificationHandler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.os.BatteryManager
import com.example.weathertriggerapp2.broadcast.Notification
import java.util.Calendar


// https://www.geeksforgeeks.org/schedule-notifications-in-android/
// https://stackoverflow.com/questions/14980899/how-to-set-time-to-24-hour-format-in-calendar
// https://www.geeksforgeeks.org/get-battery-level-in-android-using-jetpack-compose/

/**
 * Class for scheduling weather update notification
 * */
class WeatherNotification(val context: Context) {
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleWeatherNotification() {
        val intent = Intent(context, Notification::class.java).apply {
            action = Notification.ACTION_ALARM_3
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            3,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val batteryManager = context.getSystemService(BATTERY_SERVICE) as BatteryManager
        val batteryCharge: Int = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        val calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 0)

        val time = calendar.timeInMillis

        // Only schedule of battery charge is over 35% as GPS tends to kill battery
        if(batteryCharge >= 35) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
        }
    }
}