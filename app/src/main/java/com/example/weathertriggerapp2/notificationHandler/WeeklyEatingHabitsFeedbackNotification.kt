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
import com.example.weathertriggerapp2.repository.CalorieCountRepository
import com.example.weathertriggerapp2.repository.CalorieRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar

class WeeklyEatingHabitsFeedbackNotification(val context: Context) {
    fun getCurrWeek(): Int {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }
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
        calendar.set(Calendar.MINUTE, 30)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }

        val time = calendar.timeInMillis

        try{
            val calorieRepository = CalorieRepository(CalorieDatabase.getDatabase(context).calorieDao())
            val currNumWeek = getCurrWeek()
            GlobalScope.launch(Dispatchers.IO) {
                val weeklySugar = calorieRepository.getWeeklySugarCount(currNumWeek)
                val weeklyFat = calorieRepository.getWeeklyFatCount(currNumWeek)
                val daysRecorded = calorieRepository.getDaysRecorded(currNumWeek)

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