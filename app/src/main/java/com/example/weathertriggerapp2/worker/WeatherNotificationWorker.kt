package com.example.weathertriggerapp2.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weathertriggerapp2.R
import com.example.weathertriggerapp2.data.WeatherResponse
import com.example.weathertriggerapp2.network.WeatherApi
import retrofit2.Response
import java.util.Locale

// Practise Class - Not Needed Anymore
//class WeatherNotificationWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
//
//    private fun createNotification(weatherUiState: String) {
//        val context = applicationContext
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                "weather_channel",
//                "Weather Updates",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val notificationBuilder = NotificationCompat.Builder(context, "weather_channel")
//            .setContentTitle("Weather Update")
//            .setContentText(getNotificationMessage(weatherUiState))
//            .setSmallIcon(R.drawable.ic_launcher_background)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setAutoCancel(true) // Automatically dismiss the notification when tapped
//
//        notificationManager.notify(2, notificationBuilder.build())
//    }
//
//    private fun getNotificationMessage(weather: String): String {
//        return when (weather.lowercase(Locale.getDefault())) {
//            "clouds" -> "Weather seems to be overcast. Why don't you try indoor exercises today?"
//            "clear" -> "Weather near you is sunny today! Do you have time for a walk?"
//            "rain" -> "It is raining all day near you. Try indoor exercises to keep you motivated throughout the day!"
//            else -> "Weather: $weather"
//        }
//    }
//
//
//    override suspend fun doWork(): Result {
//        var weatherResponse by mutableStateOf<WeatherResponse?>(null)
//        val response: Response<WeatherResponse> = WeatherApi.retrofitService.getWeather().execute()
//        weatherResponse = response.body()
//
//        if(weatherResponse != null) {
//            val main = weatherResponse!!.weather.firstOrNull()?.main
//            if (main != null) {
//                createNotification(main)
//            }
//            return Result.success()
//        }
//        return Result.retry()
//    }
//
//}



