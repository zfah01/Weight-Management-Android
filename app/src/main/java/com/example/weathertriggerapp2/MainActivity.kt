package com.example.weathertriggerapp2

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.weathertriggerapp2.ui.theme.WeatherTriggerApp2Theme
import com.example.weathertriggerapp2.viewModel.MainScreen
import com.example.weathertriggerapp2.viewModel.WeatherViewModel
import com.example.weathertriggerapp2.worker.WeatherAndLocationNotificationWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
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

            setContent {
                WeatherTriggerApp2Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
//                        val weatherViewModel: WeatherViewModel = viewModel()
//                        MainScreen(weatherUiState = weatherViewModel.weatherUiState)
                    }
                }
            }

    }

    private fun scheduleNotificationWorker() {
        val constraints = Constraints.Builder() // only run if battery isn't low - location/gps tends to kill battery
            .setRequiresBatteryNotLow(true)
            .build()

        // Will Change to AlarmManager to get sending every day at 9am
        val workRequestLocationAndWeather = PeriodicWorkRequest.Builder(
            WeatherAndLocationNotificationWorker::class.java,
            15,
            TimeUnit.MINUTES
        ).setConstraints(constraints).build()

        WorkManager.getInstance(applicationContext).enqueue(workRequestLocationAndWeather)
    }

    // Checks if permissions are granted. If so, schedule notification worker
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // location permission
        if (requestCode == 0) {
            if (grantResults.all { it == PackageManager.PERMISSION_DENIED }) {
                locationPermissionDeniedAlert()
            }
            else {
                // Schedule workers
                scheduleNotificationWorker()
            }
        }
    }

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