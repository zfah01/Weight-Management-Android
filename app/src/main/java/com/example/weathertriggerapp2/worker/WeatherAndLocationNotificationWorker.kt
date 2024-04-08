package com.example.weathertriggerapp2.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weathertriggerapp2.R
import com.example.weathertriggerapp2.data.WeatherResponse
import com.example.weathertriggerapp2.locationHandler.DefaultLocationClient
import com.example.weathertriggerapp2.network.WeatherApi
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.util.Locale

class WeatherAndLocationNotificationWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private fun createNotification(weather: String) {
        val context = applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
        val notification = NotificationCompat.Builder(context, "weather_location_channel")
            .setContentTitle("Daily Weather Update")
            .setContentText(getNotificationMessage(weather))
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(3, notification.build())
    }

    private fun getNotificationMessage(weather: String): String {
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

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO){
            return@withContext try {
                // Initialise locationClient
                val locationClient = DefaultLocationClient(
                    applicationContext,
                    LocationServices.getFusedLocationProviderClient(applicationContext)
                )

                // Get updated location
                val locationUpdates = locationClient.getLocationUpdates(300000) // every 5 mins

                // Get latitude and longitude value from updated location
                locationUpdates.collect { location ->
                    val lat = location.latitude
                    val long = location.longitude
                    val appid = "ed339cdb731796705ce70f8b33f20291"

                    var weatherResponse by mutableStateOf<WeatherResponse?>(null)
                    val response: Response<WeatherResponse> =
                        WeatherApi.retrofitService.getWeatherLocation(lat, long, appid).execute()
                    if (response.isSuccessful) {
                        weatherResponse = response.body()
                    } else {
                        // Get most recent cached response? Right now just doesn't run if it fails
                        weatherResponse = null
                        Log.e("TAG", "Error Getting Response")
                    }

                    if (weatherResponse != null) {
                        val main = weatherResponse!!.weather.firstOrNull()?.main
                        if (main != null) {
                            createNotification(main)
                        }
                    }
                }
                Result.success()
            } catch (e: Exception) {
                Log.e("TAG", "${e.message}")
                Result.failure()
            }

        }
    }
}