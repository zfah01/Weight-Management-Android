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
import com.example.weathertriggerapp2.locationHandler.DefaultLocationClient
import com.example.weathertriggerapp2.network.WeatherApi
import com.example.weathertriggerapp2.repository.CalorieCountRepository
import com.example.weathertriggerapp2.repository.CalorieRepository
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Locale

// BroadcastReceiver for handling notifications
class Notification : BroadcastReceiver() {

    // Method called when the broadcast is received based on intent action
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        var calorieCount = CalorieCountRepository.calorieCount
        val fatIntake = CalorieCountRepository.saturatedFatCount
        val sugarIntake = CalorieCountRepository.sugarCount
        val stepCount = CalorieCountRepository.stepCount
        val goalSteps = CalorieCountRepository.goalSteps
        val goalIncreased = CalorieCountRepository.goalIncreased

        fun roundOffDecimal(distance: Double?): Any {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            return df.format(distance).toDouble()
        }

        var gender = ""
        val store = UserDataStore(context)
        store.getAccessToken.onEach { accessToken ->
            Log.d("TAG", "Access Token: $accessToken")
            gender = accessToken
        }.launchIn(GlobalScope)

        when (intent.action) {
            ACTION_ALARM_1 -> {
                if (calorieCount == null) {
                    calorieCount = 0.0
                }
                createMiddayCalorieNotification(context, calorieCount)
            }

            ACTION_ALARM_2 -> {
                fun createEndOfDayCaloriesNotification() {
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
                        .setContentText(calorieCount?.let { getEndOfDayNotificationMessage(it, gender) })
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)

                    notificationManager.notify(2, notification.build())
                }
                createEndOfDayCaloriesNotification()
            }

            ACTION_ALARM_3 -> {
                fun createWeatherNotification(weather: String) {
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
                        .setStyle(NotificationCompat.BigTextStyle()
                            .bigText(getWeatherNotificationMessage(weather)))
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)

                    notificationManager.notify(3, notification.build())
                }

                try {
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

                            val response = WeatherApi(context).weatherRepository.getWeatherLocation(appid, lat, long)
                            var main = response.weather.firstOrNull()?.main
                            if (main != null) {
                                createWeatherNotification(main)
                            } else {
                                main = "no response"
                                createWeatherNotification(main)
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
                fun createWeeklyFeedbackNotification(calorieCount : Double, weeklySteps : Double) {
                    Log.i("TAG", "CREATE NOTIFICATION WEEKLY CALORIES FEEDBACK")
                    Log.i("TAG", "CALORIES: $calorieCount STEPS: $weeklySteps")
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
                            .bigText(getWeeklyFeedbackMessage(calorieCount, weeklySteps, gender)))
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)

                    notificationManager.notify(5, notification.build())
                }

                try {
                    val calorieRepository = CalorieRepository(CalorieDatabase.getDatabase(context).calorieDao())
                    val currNumWeek = getCurrWeek()
                    GlobalScope.launch(Dispatchers.IO) {
                        val weeklyCalories = calorieRepository.getWeeklyCalorieCount(currNumWeek -1)
                        val weeklySteps = calorieRepository.getStepsCountWeekly(currNumWeek -1)
                        val daysRecorded = calorieRepository.getDaysRecorded(currNumWeek -1)

                        val averageCalories = weeklyCalories / daysRecorded.toDouble()
                        val averageSteps = weeklySteps / daysRecorded.toDouble()

                        createWeeklyFeedbackNotification(averageCalories, averageSteps)
                        calorieRepository.delete(currNumWeek-1)
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "${e.message}")
                }
            }
            ACTION_ALARM_6 -> {
                fun createSugarAndFatNotification(dailySugar : Double, dailyFat : Double) {
                    Log.i("TAG", "CREATE NOTIFICATION WEEKLY CALORIES FEEDBACK")
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    // Create Notification Channel
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
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)

                    notificationManager.notify(6, notification.build())
                }

                try{
                    val calorieRepository =
                        CalorieRepository(CalorieDatabase.getDatabase(context).calorieDao())
                    val currNumWeek = getCurrWeek()
                    GlobalScope.launch(Dispatchers.IO) {
                        val weeklySugar = calorieRepository.getWeeklySugarCount(currNumWeek -1)
                        val weeklyFat = calorieRepository.getWeeklyFatCount(currNumWeek -1)
                        val daysRecorded = calorieRepository.getDaysRecorded(currNumWeek -1)

                        val dailySugar = weeklySugar / daysRecorded.toDouble()
                        val dailyFat = weeklyFat / daysRecorded.toDouble()
                        createSugarAndFatNotification(dailySugar, dailyFat)
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "${e.message}")
                }
            }
            ACTION_ALARM_7 -> {
                fun getDistanceNotificationMessage(): String {
                    val currDistance = (stepCount?.times(0.00074))
                    val currDistanceRounded = roundOffDecimal(currDistance)
                    if (goalSteps != null) {
                        val goalDistance = (goalSteps.times(0.00074))
                        return if(goalSteps > 0) {
                            val difference = goalDistance - currDistance!!
                            val differenceRounded = roundOffDecimal(difference)
                            "You are $differenceRounded km short of meeting yesterday's distance. Let's try and walk another kilometer!"
                        } else{
                            "You've walked $currDistanceRounded km today! Let's keep going and walk another kilometer!"
                        }
                    }
                    return "You've walked $currDistanceRounded km today! Let's keep going and walk another kilometer!"
                }
                fun createDistanceNotification() {
                    Log.i("TAG", "CREATE NOTIFICATION DISTANCE AFTERNOON")
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
                                .setContentText(calorieCount?.let { getDistanceNotificationMessage() })
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true)

                            notificationManager.notify(6, notification.build())
                        }
                        if(stepCount < goalSteps!! && !goalIncreased) {
                            val notification = NotificationCompat.Builder(
                                context,
                                "distance_afternoon_channel"
                            )
                                .setContentTitle("Afternoon Distance Check In")
                                .setContentText(calorieCount?.let { getDistanceNotificationMessage() })
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true)

                            notificationManager.notify(6, notification.build())
                        }
                    }
                }
                Log.i("TAG", "HERE")
                createDistanceNotification()

            }
        }
    }

    fun createMiddayCalorieNotification(context: Context, calorieCount : Double) {
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

        var message = ""

        if (calorieCount != null) {
            message = if(calorieCount > 0.0) {
                "So far, you have consumed $calorieCount calories! Keep it up!"
            } else {
                "Have you added your calorie intake for today yet? Remember to record it accurately track your progress!"
            }
        }
        // Create notification
        val notification = NotificationCompat.Builder(
            context,
            "calorie_midday_channel"
        )
            .setContentTitle("Midday Calorie Check In")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(1, notification.build())
    }

    fun getEndOfDayNotificationMessage(caloriesAmount: Double, gender: String): String {
        val minCalorieRange : Double = if(gender == "Male") {
            1900.0
        } else {
            1400.0
        }
        val maxCalorieRange : Double = if(gender == "Male") {
            2100.0
        } else {
            1600.0
        }
        return if ((gender == "Male" && caloriesAmount in minCalorieRange..maxCalorieRange) ||
            (gender == "Female" && caloriesAmount in minCalorieRange..maxCalorieRange)) {
            "You have consumed $caloriesAmount calories today and met your daily calorie target! Well done!"
        } else {
            "You have consumed $caloriesAmount calories today! \n" +
                    "Unfortunately, you didn't quite meet your calorie target for the day. Remember, maintaining a balanced diet is crucial for your overall well-being!"
        }
    }

    val outdoorActivities = arrayOf("walk", "run", "yoga session")
    val indoorActivities = arrayOf("burpees", "push-ups", "arm extensions", "tummy twists")
    fun getWeatherNotificationMessage(weather: String): String {
        return when (weather.lowercase(Locale.getDefault())) {
            "clear" -> "Weather near you is clear today! Do you have time for a ${outdoorActivities.randomOrNull()} today?"
            "no response" -> "Hmm, something went wrong with fetching your location. In the meantime, why don't you try some indoor exercises today like ${indoorActivities.randomOrNull()} or ${indoorActivities.randomOrNull()}?"
            else -> "Weather near you seems to be poor today. Why don't you try some indoor exercises today like ${indoorActivities.randomOrNull()} or ${indoorActivities.randomOrNull()} today?"
        }
    }

    fun getSugarAndFatNotificationMessage(dailySugar: Double, dailyFat : Double): String {
        val exceededSugarIntake = dailySugar - 30.0
        val exceededFatIntake = dailyFat - 30.0
        var sugarMessage = ""
        var fatMessage = ""
        if(dailySugar > 30.0) {
            sugarMessage = "You have exceeded your daily sugar limit of 30g by ${exceededSugarIntake.toInt()} last week. Try opting for water instead of fizzy drink or having less sugar in your daily cups of tea or coffee!"
//                                "\nFor more information, visit: https://www.nhs.uk/live-well/eat-well/food-types/how-does-sugar-in-our-diet-affect-our-health/#:~:text=Adults%20should%20have%20no%20more,day%20(5%20sugar%20cubes) "
        }
        if(dailyFat > 30.0) {
            fatMessage = "You have exceeded your daily saturated fats limit by ${exceededFatIntake.toInt()} last week. Try opting for less fatty foods, such as fatty meats or butter to improve heart health."
//                                "\nFor more information, visit: https://www.nhs.uk/live-well/eat-well/food-types/different-fats-nutrition/#:~:text=Saturated%20fat%20guidelines&text=The%20government%20recommends%20that%3A,children%20should%20have%20less"
        }

        if(sugarMessage.isEmpty() && fatMessage.isNotEmpty()) {
            return fatMessage
        }
        else if (sugarMessage.isNotEmpty() && fatMessage.isEmpty()) {
            return sugarMessage
        }
        else {
            return fatMessage + "\n" + sugarMessage
        }
    }

    fun getWeeklyFeedbackMessage(caloriesAmount: Double, weeklySteps : Double, gender: String): String {
        if(!caloriesAmount.isNaN() || !weeklySteps.isNaN()) {
            val currDistance = (weeklySteps?.times(0.00074))
            val minCalorieRange: Double = if (gender == "Male") {
                1900.0
            } else {
                1400.0
            }
            val maxCalorieRange: Double = if (gender == "Male") {
                2100.0
            } else {
                1600.0
            }
            val foodHabitsMessage: String =
                if (caloriesAmount in minCalorieRange..maxCalorieRange) {
                    "Well Done! You have consumed a healthy average of ${caloriesAmount.toInt()} calories each day this week."

                } else {
                    "You haven't met your daily calorie intake this week! You consumed an average of ${caloriesAmount.toInt()} calories. " +
                            "Remember a healthy daily caloric is between $minCalorieRange - $maxCalorieRange calories if you are aiming for healthy weight loss. \n"
//                                "For a more accurate daily caloric intake, you are able to calculate this via: https://www.calculator.net/calorie-calculator.html"
                }

            val activityMessage: String = if (currDistance!! >= 2.5) {
                "Congratulations! You have hit an average of ${currDistance.toInt()} steps per day! Keep it up!"
            } else {
                "You have hit an average of ${currDistance.toInt()} steps per day! Why don't you try going for another kilometer?"
            }


            return foodHabitsMessage + "\n" + activityMessage
        }
        else {
            return "Seems like we don't have enough data yet! Keep up with the good work to build your profile!"
        }
    }

    fun getCurrWeek(): Int {
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
        const val ACTION_ALARM_7 = "Distance Target"
    }
}