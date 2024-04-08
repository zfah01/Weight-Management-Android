package com.example.weathertriggerapp2.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weathertriggerapp2.R
import com.example.weathertriggerapp2.locationHandler.DefaultLocationClient
import com.google.android.gms.location.LocationServices

// Practise Class - Not Needed Anymore
//class LocationNotificationWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
//
//    private fun createNotification(lat: String, long:String) {
//        val context = applicationContext
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                "location_channel",
//                "Location Update",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val notificationBuilder = NotificationCompat.Builder(context, "weather_channel")
//            .setContentTitle("Location Update")
//            .setContentText(getNotificationMessage(lat, long))
//            .setSmallIcon(R.drawable.ic_launcher_background)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setAutoCancel(true)
//
//        notificationManager.notify(1, notificationBuilder.build())
//    }
//
//    private fun getNotificationMessage(lat: String, long: String): String {
//        return "Your Current Location is $lat, $long."
//    }
//
//    override suspend fun doWork(): Result {
//        try {
//            val locationClient = DefaultLocationClient(
//                applicationContext,
//                LocationServices.getFusedLocationProviderClient(applicationContext)
//            )
//
//            val locationUpdates = locationClient.getLocationUpdates(300000) // every 5 mins
//
//            locationUpdates.collect { location ->
//                val lat = location.latitude.toString()
//                val long = location.longitude.toString()
//                createNotification(lat, long)
//            }
//
//            return Result.success()
//        } catch (e: Exception) {
//            return Result.failure()
//        }
//    }
//}