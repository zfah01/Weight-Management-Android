package com.example.weathertriggerapp2

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.weathertriggerapp2.ui.theme.WeatherTriggerApp2Theme
import com.example.weathertriggerapp2.viewModel.MainScreen


import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weathertriggerapp2.notificationHandler.CalorieEndOfDayNotification
import com.example.weathertriggerapp2.notificationHandler.CalorieMidDayNotification
import com.example.weathertriggerapp2.notificationHandler.InsertTotalCalories
import com.example.weathertriggerapp2.notificationHandler.WeatherNotification

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request Permissions - pop up should appear
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 0
        )

        val alarmSchedulerCalorieMidday = CalorieMidDayNotification(applicationContext)
        val alarmSchedulerCalorieEod = CalorieEndOfDayNotification(applicationContext)
        val alarmSchedulerCalorieInsert = InsertTotalCalories(applicationContext)
        alarmSchedulerCalorieMidday.scheduleMiddayNotification()
        alarmSchedulerCalorieEod.scheduleEodNotification()
        alarmSchedulerCalorieInsert.scheduleInsertCalorieNotification()

            setContent {
                WeatherTriggerApp2Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen()
                    }
                }
            }

    }

    // Checks if permissions are granted. If so, schedule notification worker
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // location permission
        if (requestCode == 0) {
            if (grantResults.all { it == PackageManager.PERMISSION_DENIED }) {
                locationPermissionDeniedAlert()
            }
            else {
                // Schedule alarm
                Log.i("TAG", "scheduling location notification")

                val alarmSchedulerWeather = WeatherNotification(applicationContext)
                alarmSchedulerWeather.scheduleWeatherNotification()
            }
        }
    }

    // Dialog alert if permissions are denied
    private fun locationPermissionDeniedAlert() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Has Been Denied")
            .setMessage(
                "This app requires access to your location to provide weather data accurately. \n\n" +
                "To enable the weather notification, please ensure location permissions are granted on your device. " +
                "Otherwise it will not run."
            )
            .setPositiveButton("OK") { dialog, _->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}