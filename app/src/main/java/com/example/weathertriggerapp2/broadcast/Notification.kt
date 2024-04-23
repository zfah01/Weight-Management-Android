package com.example.weathertriggerapp2.broadcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.weathertriggerapp2.R
import com.example.weathertriggerapp2.data.Calorie
import com.example.weathertriggerapp2.data.CalorieDatabase
import com.example.weathertriggerapp2.locationHandler.DefaultLocationClient
import com.example.weathertriggerapp2.network.WeatherApi
import com.example.weathertriggerapp2.repository.CalorieCountRepository
import com.example.weathertriggerapp2.repository.CalorieRepository
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

// BroadcastReceiver for handling notifications
class Notification : BroadcastReceiver() {

    // Method called when the broadcast is received based on intent action
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        val calorieCount = CalorieCountRepository.calorieCount
        val fatIntake = CalorieCountRepository.saturatedFatCount
        val sugarIntake = CalorieCountRepository.sugarCount
        val stepCount = CalorieCountRepository.stepCount
        when (intent.action) {
            ACTION_ALARM_1 -> {
                fun createNotification() {
                    Log.i("TAG", "CREATE NOTIFICATION MIDDAY CALORIE")
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    // Create Notification Channel
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            "calorie_midday_channel",
                            "Calorie Midday Check In",
                            NotificationManager.IMPORTANCE_DEFAULT
                        ).apply {
                            description = "Calorie Midday Reminder Channel"
                        }
                        notificationManager.createNotificationChannel(channel)
                    }

                    // Create notification
                    val notification = NotificationCompat.Builder(
                        context,
                        "calorie_midday_channel"
                    )
                        .setContentTitle("Midday Calorie Check In")
                        .setContentText("So far, you have consumed $calorieCount calories! Keep it up!")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)

                    notificationManager.notify(1, notification.build())
                }
                createNotification()
            }

            ACTION_ALARM_2 -> {
                fun getNotificationMessage(caloriesAmount: Double): String {
                    return if (caloriesAmount >= 2000.0) {
                        "You have consumed $calorieCount calories today and met your daily calorie target! Well done!"
                    } else {
                        "You have consumed $calorieCount calories today! \n" +
                                "Unfortunately, you didn't quite meet your calorie target for the day. Remember, maintaining a balanced diet is crucial for your overall well-being!"
                    }
                }

                fun createNotification() {
                    Log.i("TAG", "CREATE NOTIFICATION EOD CALORIE")
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    // Create Notification Channel
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            "calorie_eod_channel",
                            "Calorie End of Day Check In",
                            NotificationManager.IMPORTANCE_DEFAULT
                        ).apply {
                            description = "Calorie Midday Reminder Channel"
                        }
                        notificationManager.createNotificationChannel(channel)
                    }

                    // Create notification
                    val notification = NotificationCompat.Builder(
                        context,
                        "calorie_eod_channel"
                    )
                        .setContentTitle("End of Day Calorie Check In")
                        .setContentText(calorieCount?.let { getNotificationMessage(it) })
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)

                    notificationManager.notify(2, notification.build())
                }
                createNotification()
            }

            ACTION_ALARM_3 -> {
                fun getNotificationMessage(weather: String): String {
                    return when (weather.lowercase(Locale.getDefault())) {
                        "clouds" -> "Weather seems to be overcast. Why don't you try indoor exercises today like burpees or push-ups?"
                        "clear" -> "Weather near you is clear today! Do you have time for a walk today or a hike?"
                        "rain" -> "It is raining all day near you. Try indoor exercises, like burpees or push-ups, to keep you motivated throughout the day!"
                        "thunderstorm" -> "Seems like there's a thunderstorm near you. Stay safe and exercise indoors!"
                        "drizzle" -> "Today's weather is light rain. Do you have time for a walk today?"
                        "snow" -> "Look's like snow today. Do you have time for a walk today?"
                        else -> "Weather: $weather"
                    }
                }

                fun createNotification(weather: String) {
                    Log.i("TAG", "CREATE NOTIFICATION")
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    // Create Notification Channel
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            "weather_location_channel",
                            "Weather with Location Updates",
                            NotificationManager.IMPORTANCE_DEFAULT
                        ).apply {
                            description = "Weather Reminder Channel"
                        }
                        notificationManager.createNotificationChannel(channel)
                    }

                    // Create notification
                    val notification = NotificationCompat.Builder(
                        context,
                        "weather_location_channel"
                    )
                        .setContentTitle("Daily Weather Update")
                        .setContentText(getNotificationMessage(weather))
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)

                    notificationManager.notify(3, notification.build())
                }

                try {
                    Log.i("TAG", "IN TRY")
                    // Initialise locationClient
                    val locationClient = DefaultLocationClient(
                        context,
                        LocationServices.getFusedLocationProviderClient(context)
                    )

                    // Get updated location
                    val locationUpdates = locationClient.getLocationUpdates(300000) // every 5 mins

                    // Get latitude and longitude value from updated location
                    GlobalScope.launch {
                        locationUpdates.collect { location ->
                            val lat = location.latitude
                            val long = location.longitude
                            val appid = "ed339cdb731796705ce70f8b33f20291"

                            val response =
                                WeatherApi(context).weatherRepository.getWeatherLocation(appid, lat, long)
                            val main = response.weather.firstOrNull()?.main
                            if (main != null) {
                                createNotification(main)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "${e.message}")
                }
            }

            ACTION_ALARM_4 -> {
                try {
                    val currNumWeek = getCurrWeek()
                    val calorieRepository = CalorieRepository(CalorieDatabase.getDatabase(context).calorieDao())
                    val newCalorie = Calorie(0, calorieCount.toString(), stepCount.toString(), fatIntake.toString(), sugarIntake.toString(), currNumWeek)
                    GlobalScope.launch(Dispatchers.IO) {
                        calorieRepository.insert(newCalorie)
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "${e.message}")
                }
            }

            ACTION_ALARM_5 -> {
                fun getNotificationMessage(caloriesAmount: Double, weeklySteps : Double): String {
                    val foodHabitsMessage: String = if (caloriesAmount in 2000.0..2500.0) {
                        "Well Done! You have consumed a healthy average of ${caloriesAmount.toInt()} calories each day this week."

                    } else {
                        "You haven't met your daily calorie intake this week! You consumed an average of ${caloriesAmount.toInt()} calories. " +
                                "Remember a healthy daily caloric is between 2000-2500 calories if you are aiming for healthy weight loss. \n"
//                                "For a more accurate daily caloric intake, you are able to calculate this via: https://www.calculator.net/calorie-calculator.html"
                    }

                    val activityMessage: String = if (weeklySteps >= 10000.0) {
                        "Congratulations! You have hit your weekly goal of 10,000 steps each day with an average of ${weeklySteps.toInt()}"
                    } else {
                        "You have not been able to hit your target step goal this week! You managed to achieve an average of ${weeklySteps.toInt()} per day. Don't be discouraged and keep up the good work!"
                    }


                    return foodHabitsMessage + "\n" + activityMessage
                }

                fun createNotification(calorieCount : Double, weeklySteps : Double) {
                    Log.i("TAG", "CREATE NOTIFICATION WEEKLY CALORIES FEEDBACK")
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    // Create Notification Channel
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            "weekly_calories_feedback_channel",
                            "Weekly Calories Feedback",
                            NotificationManager.IMPORTANCE_DEFAULT
                        ).apply {
                            description = "Weekly Feedback Update Channel"
                        }
                        notificationManager.createNotificationChannel(channel)
                    }

                    // Create notification
                    val notification = NotificationCompat.Builder(
                        context,
                        "weekly_calories_feedback_channel"
                    )
                        .setContentTitle("Weekly Goals Check-in")
                        .setStyle(NotificationCompat.BigTextStyle()
                            .bigText(getNotificationMessage(calorieCount, weeklySteps)))
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)

                    notificationManager.notify(5, notification.build())
                }

                try {
                    val calorieRepository = CalorieRepository(CalorieDatabase.getDatabase(context).calorieDao())
                    val currNumWeek = getCurrWeek()
                    GlobalScope.launch(Dispatchers.IO) {
                        val weeklyCalories = calorieRepository.getWeeklyCalorieCount(currNumWeek)
                        val weeklySteps = calorieRepository.getStepsCountWeekly(currNumWeek)
                        val daysRecorded = calorieRepository.getDaysRecorded(currNumWeek)

                        val averageCalories = weeklyCalories / daysRecorded.toDouble()
                        val averageSteps = weeklySteps / daysRecorded.toDouble()
                        createNotification(averageCalories, averageSteps)
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "${e.message}")
                }
            }
            ACTION_ALARM_6 -> {
                val calorieRepository = CalorieRepository(CalorieDatabase.getDatabase(context).calorieDao())
                val currNumWeek = getCurrWeek()
                GlobalScope.launch(Dispatchers.IO) {
                    val weeklySugar = calorieRepository.getWeeklySugarCount(currNumWeek)
                    val weeklyFat = calorieRepository.getWeeklyFatCount(currNumWeek)
                    val daysRecorded = calorieRepository.getDaysRecorded(currNumWeek)

                    val dailySugar = weeklySugar / daysRecorded.toDouble()
                    val dailyFat = weeklyFat / daysRecorded.toDouble()
                }
            }
        }
    }
    private fun getCurrWeek(): Int {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    companion object ActionAlarms {
        const val ACTION_ALARM_1 = "Calories Midday"
        const val ACTION_ALARM_2 = "Calories EOD"
        const val ACTION_ALARM_3 = "Weather"
        const val ACTION_ALARM_4 = "Insert Calories"
        const val ACTION_ALARM_5 = "Weekly Goals"
        const val ACTION_ALARM_6 = "Weekly Eating Habits"
//        const val ACTION_ALARM_7 = ""
    }
}