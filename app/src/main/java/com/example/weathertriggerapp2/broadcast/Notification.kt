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
import com.example.weathertriggerapp2.data.UserDataStore
import com.example.weathertriggerapp2.locationHandler.DefaultUserLocationClient
import com.example.weathertriggerapp2.network.WeatherApi
import com.example.weathertriggerapp2.repository.CalorieCountRepository
import com.example.weathertriggerapp2.repository.CalorieRepository
import com.example.weathertriggerapp2.util.getCurrWeek
import com.example.weathertriggerapp2.util.getDistanceNotificationMessage
import com.example.weathertriggerapp2.util.getEndOfDayNotificationMessage
import com.example.weathertriggerapp2.util.getSugarAndFatNotificationMessage
import com.example.weathertriggerapp2.util.getWeatherNotificationMessage
import com.example.weathertriggerapp2.util.getWeeklyFeedbackMessage
import com.example.weathertriggerapp2.util.indoorActivities
import com.example.weathertriggerapp2.viewModel.CalorieViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver Class for handling notifications broadcast by alarm manager in 'notificationHandler' package
 * */
class Notification : BroadcastReceiver() {
    var middayCalorie = false
    var endOfDayCalorie = false
    var weatherNotification = false
    var insertCalories = false
    var weeklyGoals = false
    var weeklyEatingHabits = false
    var distanceTarget = false
    var randomExercise = false

    /**
     * Function called when the broadcast is received based on intent action
     * */
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        var calorieCount = CalorieCountRepository.calorieCount
        val fatIntake = CalorieCountRepository.saturatedFatCount
        val sugarIntake = CalorieCountRepository.sugarCount
        val stepCount = CalorieCountRepository.stepCount
        val goalSteps = CalorieCountRepository.goalSteps
        val goalIncreased = CalorieCountRepository.goalIncreased

        var gender = ""
        val store = UserDataStore(context)

        store.getAccessToken.onEach { accessToken ->
            gender = accessToken
        }.launchIn(GlobalScope)

        when (intent.action) {
            // Midday Calorie Update
            ACTION_ALARM_1 -> {
                if (calorieCount == null) {
                    calorieCount = 0.0
                }
                createMiddayCalorieNotification(context, calorieCount)
            }

            // End Of Day Calorie Update
            ACTION_ALARM_2 -> {
                if (calorieCount != null) {
                    createEndOfDayCaloriesNotification(context, calorieCount, gender)
                }
            }

            // Exercise Suggestion based on Weather and Location
            ACTION_ALARM_3 -> {
                try {
                    weatherNotification = true
                    // Initialise locationClient
                    val locationClient = DefaultUserLocationClient(
                        context,
                        LocationServices.getFusedLocationProviderClient(context)
                    )

                    // Get updated location
                    val locationUpdates = locationClient.getLocationUpdates(300000)

                    // Get latitude and longitude value from updated location
                    GlobalScope.launch {
                        locationUpdates.collect { location ->
                            val lat = location.latitude
                            val long = location.longitude
                            val appid = "ed339cdb731796705ce70f8b33f20291"

                            val response = WeatherApi(context).weatherRepository.getWeatherLocation(appid, lat, long)
                            var main = response.weather.firstOrNull()?.main
                            if (main != null) {
                                createWeatherNotification(context, main)
                            } else {
                                main = "no response"
                                createWeatherNotification(context, main)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "${e.message}")
                }
            }

            // Insert Collected Calorie and Steps Data Into 'calories' table
            ACTION_ALARM_4 -> {
                try {
                    insertCalories = true
                    val currNumWeek = getCurrWeek()
                    val viewModel = CalorieViewModel(context)
                    val calorieRepository = CalorieRepository(CalorieDatabase.getDatabase(context).calorieDao())
                    val newCalorie = Calorie(0, calorieCount.toString(), stepCount.toString(), fatIntake.toString(), sugarIntake.toString(), currNumWeek)
                    GlobalScope.launch(Dispatchers.IO) {
                        calorieRepository.insert(newCalorie)
                    }
                    viewModel.resetViewModel()
                } catch (e: Exception) {
                    Log.e("TAG", "${e.message}")
                }
            }

            // Weekly Feedback on Average Calories and Steps
            ACTION_ALARM_5 -> {
                try {
                    weeklyGoals = true
                    val calorieRepository = CalorieRepository(CalorieDatabase.getDatabase(context).calorieDao())
                    val currNumWeek = getCurrWeek()
                    GlobalScope.launch(Dispatchers.IO) {
                        val weeklyCalories = calorieRepository.getWeeklyCalorieCount(currNumWeek -1)
                        val weeklySteps = calorieRepository.getStepsCountWeekly(currNumWeek -1)
                        val daysRecorded = calorieRepository.getDaysRecorded(currNumWeek -1)

                        val averageCalories = weeklyCalories / daysRecorded.toDouble()
                        val averageSteps = weeklySteps / daysRecorded.toDouble()

                        createWeeklyFeedbackNotification(context, averageCalories, averageSteps, gender)
                        calorieRepository.delete(currNumWeek-1)
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "${e.message}")
                }
            }
            // Weekly Feedback on Sugar and Saturated Fats Intake Exceeding 30g
            ACTION_ALARM_6 -> {
                try{
                    weeklyEatingHabits = true
                    val calorieRepository = CalorieRepository(CalorieDatabase.getDatabase(context).calorieDao())
                    val currNumWeek = getCurrWeek()
                    GlobalScope.launch(Dispatchers.IO) {
                        val weeklySugar = calorieRepository.getWeeklySugarCount(currNumWeek -1)
                        val weeklyFat = calorieRepository.getWeeklyFatCount(currNumWeek -1)
                        val daysRecorded = calorieRepository.getDaysRecorded(currNumWeek -1)

                        val dailySugar = weeklySugar / daysRecorded.toDouble()
                        val dailyFat = weeklyFat / daysRecorded.toDouble()
                        createSugarAndFatNotification(context, dailySugar, dailyFat)
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "${e.message}")
                }
            }
            // Total Distance Covered by User
            ACTION_ALARM_7 -> {
                if (stepCount != null) {
                    if (goalSteps != null) {
                        if (calorieCount != null) {
                            createDistanceNotification(context, stepCount, goalSteps, calorieCount ,goalIncreased)
                        }
                    }
                }
            }
            // Randomised Exercise suggestion - backup for ACTION_ALARM_3
            ACTION_ALARM_8 -> {
                createRandomisedExerciseNotification(context)
            }
        }
    }

    /**
     * Function to build midday calorie notification
     * */
    private fun createMiddayCalorieNotification(context: Context, calorieCount : Double) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "calorie_midday_channel",
                "Calorie Midday Check In",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Calorie Midday Reminder Channel"
            }
            notificationManager.createNotificationChannel(channel)
            middayCalorie = true
        }

        var message: String = if(calorieCount > 0.0) {
            "So far, you have consumed $calorieCount calories! Keep it up!"
        } else {
            "Have you added your calorie intake for today yet? Remember to record it accurately track your progress!"
        }

        val notification = NotificationCompat.Builder(
            context,
            "calorie_midday_channel"
        )
            .setContentTitle("Midday Calorie Check In")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setSmallIcon(R.mipmap.distance_foreground) // https://www.veryicon.com/icons/business/terminal-project/health-11.html
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(1, notification.build())
    }

    /**
     * Function to end of day calorie notification
     * */
    private fun createEndOfDayCaloriesNotification(context: Context, calorieCount: Double, gender: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

        val notification = NotificationCompat.Builder(
            context,
            "calorie_eod_channel"
        )
            .setContentTitle("End of Day Calorie Check In")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(calorieCount?.let { getEndOfDayNotificationMessage(it, gender) }))
            .setSmallIcon(R.mipmap.distance_foreground) // https://www.veryicon.com/icons/business/terminal-project/health-11.html
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(2, notification.build())
        endOfDayCalorie = true
    }

    /**
     * Function to build weather notification
     * */
    private fun createWeatherNotification(context: Context, weather: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

        val notification = NotificationCompat.Builder(
            context,
            "weather_location_channel"
        )
            .setContentTitle("Daily Weather Update")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(getWeatherNotificationMessage(weather)))
            .setSmallIcon(R.mipmap.weather_foreground) // https://www.flaticon.com/free-icons/weather-app title="weather app icons" Weather app icons created by Andrean Prabowo - Flaticon
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(3, notification.build())
        weatherNotification = true
    }

    /**
     * Function to build weekly feedback notification
     * */
    private fun createWeeklyFeedbackNotification(context: Context, calorieCount : Double, weeklySteps : Double, gender: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

        val notification = NotificationCompat.Builder(
            context,
            "weekly_calories_feedback_channel"
        )
            .setContentTitle("Weekly Goals Check-in")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(getWeeklyFeedbackMessage(calorieCount, weeklySteps, gender)))
            .setSmallIcon(R.mipmap.trophy_foreground) // https://www.flaticon.com/free-icons/soccer-cup" title="soccer cup icons">Soccer cup icons created by Freepik - Flaticon
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(5, notification.build())
        weeklyGoals = true
    }

    /**
     * Function to build weekly feedback sugar and saturated fats notification
     * */
    private fun createSugarAndFatNotification(context: Context, dailySugar : Double, dailyFat : Double) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "weekly_sugar_fat_feedback_channel",
                "Weekly Sugar/Fat Feedback",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Weekly Feedback Update Channel"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create notification
        val notification = NotificationCompat.Builder(
            context,
            "weekly_sugar_fat_feedback_channel"
        )
            .setContentTitle("Weekly Sugar and Fat Intake")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(getSugarAndFatNotificationMessage(dailySugar, dailyFat)))
            .setSmallIcon(R.mipmap.trophy_foreground) // https://www.flaticon.com/free-icons/soccer-cup" title="soccer cup icons">Soccer cup icons created by Freepik - Flaticon
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(6, notification.build())
        weeklyEatingHabits = true
    }

    /**
     * Function to build daily distance covered by user
     * */
    private fun createDistanceNotification(context: Context, stepCount: Int, goalSteps: Int, calorieCount: Double, goalIncreased: Boolean) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Create Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "distance_afternoon_channel",
                "Afternoon Distance Check In",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Afternoon Distance Reminder Channel"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create notification
        if (stepCount != null) {
            if(goalSteps == 0 || goalSteps == null) {
                val notification = NotificationCompat.Builder(
                    context,
                    "distance_afternoon_channel"
                )
                    .setContentTitle("Afternoon Distance Check In")
                    .setContentText(calorieCount?.let {
                        if (goalSteps != null) {
                            getDistanceNotificationMessage(stepCount, goalSteps)
                        }
                    }.toString())
                    .setSmallIcon(R.mipmap.running_foreground)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)

                notificationManager.notify(7, notification.build())
            }
            if(stepCount < goalSteps!! && !goalIncreased) {
                val notification = NotificationCompat.Builder(
                    context,
                    "distance_afternoon_channel"
                )
                    .setContentTitle("Afternoon Distance Check In")
                    .setContentText(calorieCount?.let { getDistanceNotificationMessage(stepCount, goalSteps) })
                    .setSmallIcon(R.mipmap.running_foreground) // https://www.veryicon.com/icons/sport/sports-series-2/running-shoes-8.html
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)

                notificationManager.notify(7, notification.build())
            }
        }
        distanceTarget = true
    }

    /**
     * Function to build randomised exercise notification
     * */
    private fun createRandomisedExerciseNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Create Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "exercise_channel",
                "Daily Exercise Recommendation",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily Exercise Recommendation"
            }
            notificationManager.createNotificationChannel(channel)
        }

        var message = "Lets get moving! Why don't you try some ${indoorActivities.randomOrNull()} today?"

        // Create notification
        val notification = NotificationCompat.Builder(
            context,
            "exercise_channel"
        )
            .setContentTitle("Daily Exercise Recommendation")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setSmallIcon(R.mipmap.running_foreground) // https://www.veryicon.com/icons/sport/sports-series-2/running-shoes-8.html
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(8, notification.build())
        randomExercise = true
    }

    /**
     * Object Class representing intents
     * */
    companion object ActionAlarms {
        const val ACTION_ALARM_1 = "Calories Midday"
        const val ACTION_ALARM_2 = "Calories EOD"
        const val ACTION_ALARM_3 = "Weather"
        const val ACTION_ALARM_4 = "Insert Calories"
        const val ACTION_ALARM_5 = "Weekly Goals"
        const val ACTION_ALARM_6 = "Weekly Eating Habits"
        const val ACTION_ALARM_7 = "Distance Target"
        const val ACTION_ALARM_8 = "Daily Exercise"
    }
}