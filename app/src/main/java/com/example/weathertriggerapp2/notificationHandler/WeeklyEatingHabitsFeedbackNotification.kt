package com.example.weathertriggerapp2.notificationHandler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.weathertriggerapp2.broadcast.Notification
import com.example.weathertriggerapp2.data.CalorieDatabase
import com.example.weathertriggerapp2.repository.CalorieRepository
import com.example.weathertriggerapp2.util.getCurrWeek
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Class for scheduling sugar and saturated fats notification
 * */
class WeeklyEatingHabitsFeedbackNotification(val context: Context) {
    /**
     * Function for scheduling notification at 12pm every Monday if the user's average saturated fat or sugar intake is
     * over 30 grams (g) every Monday
     * */
    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleWeeklyEatingHabitsNotification() {
        val intent = Intent(context, Notification::class.java).apply {
            action = Notification.ACTION_ALARM_6
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            6,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }

        val time = calendar.timeInMillis

        try{
            val calorieRepository = CalorieRepository(CalorieDatabase.getDatabase(context).calorieDao())
            val currNumWeek = getCurrWeek()
            GlobalScope.launch(Dispatchers.IO) {
                val weeklySugar = calorieRepository.getWeeklySugarCount(currNumWeek - 1)
                val weeklyFat = calorieRepository.getWeeklyFatCount(currNumWeek - 1)
                val daysRecorded = calorieRepository.getDaysRecorded(currNumWeek - 1)

                val dailySugar = weeklySugar / daysRecorded.toDouble()
                val dailyFat = weeklyFat / daysRecorded.toDouble()

                if(dailyFat > 30.0 || dailySugar > 30.0){
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        time,
                        pendingIntent
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "${e.message}")
        }
    }
}