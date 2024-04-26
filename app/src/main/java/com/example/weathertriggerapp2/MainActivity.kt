package com.example.weathertriggerapp2

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent


import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weathertriggerapp2.data.HalfwayWorker
import com.example.weathertriggerapp2.notificationHandler.CalorieEndOfDayNotification
import com.example.weathertriggerapp2.notificationHandler.CalorieMidDayNotification
import com.example.weathertriggerapp2.calorieValuesHandler.InsertTotalCalories
import com.example.weathertriggerapp2.notificationHandler.DistanceByAfternoon
import com.example.weathertriggerapp2.notificationHandler.WeatherNotification
import com.example.weathertriggerapp2.notificationHandler.WeeklyEatingHabitsFeedbackNotification
import com.example.weathertriggerapp2.notificationHandler.WeeklyGoalsFeedbackNotification
import com.example.weathertriggerapp2.repository.CalorieCountRepository
import com.example.weathertriggerapp2.viewModel.CalorieViewModel
import java.util.Calendar
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null;

    private var running = false
    private var magnitudePrevious = 0.0
    private var stepCount = 0
    private var lastReset = Calendar.getInstance()

    private var stepGoal = 0

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
        val alarmSchedulerWeeklyFeedback = WeeklyGoalsFeedbackNotification(applicationContext)
        val alarmSchedulerWeeklyEatingHabitsFeedback = WeeklyEatingHabitsFeedbackNotification(applicationContext)
        val alarmSchedulerDistanceByAfternoon = DistanceByAfternoon(applicationContext)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        CalorieCountRepository.goalSteps = stepGoal

        alarmSchedulerDistanceByAfternoon.scheduleAfternoonNotification()
        alarmSchedulerCalorieMidday.scheduleMiddayNotification()
        alarmSchedulerCalorieEod.scheduleEodNotification()
        alarmSchedulerCalorieInsert.scheduleInsertCalorieNotification()
        alarmSchedulerWeeklyFeedback.scheduleWeeklyGoalsNotification()
        alarmSchedulerWeeklyEatingHabitsFeedback.scheduleWeeklyEatingHabitsNotification()

            setContent {
                WeatherTriggerApp2Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen(applicationContext)
                    }
                }
            }

    }

    override fun onResume() {
        super.onResume()
        running = true
        val movementSensor : Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        if(movementSensor == null) {
            Toast.makeText(this, "No sensor on this device", Toast.LENGTH_SHORT).show()
        }
        else{
            sensorManager?.registerListener(this, movementSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

//    @SuppressLint("SimpleDateFormat")
    override fun onSensorChanged(event: SensorEvent?) {
        if(running){
            val currDate = Calendar.getInstance();

            if(currDate.get(Calendar.YEAR) != lastReset.get(Calendar.YEAR)
                || currDate.get(Calendar.MONTH) != lastReset.get(Calendar.MONTH)
                || currDate.get(Calendar.DAY_OF_MONTH) != lastReset.get(Calendar.DAY_OF_MONTH)
            ){
                resetSteps()
            }


            var xacceleration = event?.values?.get(0)
            var yacceleration = event?.values?.get(1)
            var zacceleration = event?.values?.get(2)

            var magnitude = sqrt((xacceleration!! *xacceleration + yacceleration!! *yacceleration + zacceleration!! *zacceleration).toDouble())

            var magnitudeDelta = magnitude - magnitudePrevious

            magnitudePrevious = magnitude

            if(magnitudeDelta > 6){
                stepCount++
                updateStepCount(stepCount)
                if(stepCount == stepGoal){
                    val distance = stepGoal * 0.00074
                    createWorkRequest("You've matched yesterdays distance of $distance km! Go you! WHy don't we try for one more kilometer?")
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun createWorkRequest(message: String) {
        val myWorkRequest = OneTimeWorkRequestBuilder<HalfwayWorker>()
            .setInputData(
                workDataOf(
                    "title" to "Daily Steps",
                    "message" to message,
                )
            )
            .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest)
    }

    fun updateStepCount(newValue: Int) {
        CalorieCountRepository.stepCount = newValue
        Log.i("TAG", "COUNT: " + CalorieCountRepository.stepCount)
    }

    private fun resetSteps(){
        stepGoal = stepCount
        CalorieCountRepository.goalSteps = stepCount
        stepCount = 0;
        lastReset = Calendar.getInstance();
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
    fun locationPermissionDeniedAlert() {
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