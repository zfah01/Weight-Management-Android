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
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.media.audiofx.BassBoost


import android.os.Build
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.Snackbar
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Calendar
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null;

    private var running = false
    private var sensorPermission = false
    private var magnitudePrevious = 0.0
    private var stepCount = 0
    private var lastReset = Calendar.getInstance()

    private var stepGoal = 10
    private var goalIncreased = false

    var notificationCheck =  false


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_DENIED){
//            requestPermissions(String{Mainfest.permission.POST_NOTIFICATIONS}, 1)
//        }
        showNotificationPermissionRationale()

        Log.i("TAG", notificationCheck.toString())
        if(notificationCheck){
            Log.i("TAG", "NOTIFICATIONS SET")
            // Request Permissions - pop up should appear
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ), 0
                )
            }

            val alarmSchedulerCalorieMidday = CalorieMidDayNotification(applicationContext)
            val alarmSchedulerCalorieEod = CalorieEndOfDayNotification(applicationContext)
            val alarmSchedulerCalorieInsert = InsertTotalCalories(applicationContext)
            val alarmSchedulerWeeklyFeedback = WeeklyGoalsFeedbackNotification(applicationContext)
            val alarmSchedulerWeeklyEatingHabitsFeedback = WeeklyEatingHabitsFeedbackNotification(applicationContext)

            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

            CalorieCountRepository.goalSteps = stepGoal
            CalorieCountRepository.goalIncreased = goalIncreased

            alarmSchedulerCalorieMidday.scheduleMiddayNotification()
            alarmSchedulerCalorieEod.scheduleEodNotification()
            alarmSchedulerCalorieInsert.scheduleInsertCalorieNotification()
            alarmSchedulerWeeklyFeedback.scheduleWeeklyGoalsNotification()
            alarmSchedulerWeeklyEatingHabitsFeedback.scheduleWeeklyEatingHabitsNotification()
        }


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
    private fun showNotificationPermissionRationale() {

        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage("Notification permission is required, to show notification")
            .setPositiveButton("Yes") { _, _ ->
                Log.i("TAG", "HELO: " )
                    notificationCheck = true
                Log.i("TAG", notificationCheck.toString() )
            }
            .setNegativeButton("No", null)
            .show()
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
        if(running && sensorPermission){
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
                    val distanceRoundUp = roundOffDecimal(distance)
                    if(!goalIncreased){
                        createWorkRequest("You've matched yesterday's distance of $distanceRoundUp km! Go you! Why don't we try for one more kilometer?")
                        stepGoal += 1352
                        goalIncreased = true
                        CalorieCountRepository.goalIncreased = true
                    }
                    else{
                        createWorkRequest("You've hit your new distance goal of $distanceRoundUp km! Go you! Why don't we try for one more kilometer?")
                        stepGoal += 1352
                    }
                }
            }
        }
    }

    private fun roundOffDecimal(distance : Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(distance).toDouble()
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

    private fun updateStepCount(newValue: Int) {
        CalorieCountRepository.stepCount = newValue
        Log.i("TAG", "COUNT: " + CalorieCountRepository.stepCount)
    }

    private fun resetSteps(){
        stepGoal = stepCount
        goalIncreased = false
        CalorieCountRepository.goalSteps = stepCount
        CalorieCountRepository.goalIncreased = false
        stepCount = 0;
        lastReset = Calendar.getInstance();
    }


    // Checks if permissions are granted. If so, schedule notification worker
    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // location permission
        if (requestCode == 0) {
            if (grantResults.all { it == PackageManager.PERMISSION_DENIED }) {
                locationPermissionDeniedAlert()
                acceleratorPermissionDeniedAlert()
            }
            else {
                for (permission in permissions){
                    val checkVal = ContextCompat.checkSelfPermission(applicationContext, permission)
                    if(permission == Manifest.permission.ACTIVITY_RECOGNITION){
                        if(checkVal == PackageManager.PERMISSION_GRANTED){
                            Log.i("TAG", "scheduling accelerometer")

                            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

                            sensorPermission = true
                            val alarmSchedulerDistanceByAfternoon = DistanceByAfternoon(applicationContext)
                            alarmSchedulerDistanceByAfternoon.scheduleAfternoonNotification()
                        }
                        else{
                            acceleratorPermissionDeniedAlert()
                        }
                    }
                    else if(permission == Manifest.permission.ACCESS_COARSE_LOCATION || permission == Manifest.permission.ACCESS_FINE_LOCATION){
                        if(checkVal == PackageManager.PERMISSION_GRANTED){
                            // Schedule alarm
                            Log.i("TAG", "scheduling location notification")
                            val alarmSchedulerWeather = WeatherNotification(applicationContext)
                            alarmSchedulerWeather.scheduleWeatherNotification()
                        }
                        else{
                            locationPermissionDeniedAlert()
                        }
                    }
                }
            }
        }
    }

    private fun acceleratorPermissionDeniedAlert() {
        AlertDialog.Builder(this)
            .setTitle("Accelerator Permission Has Been Denied")
            .setMessage(
                "This app requires access to your accelerator sensor to provide movement data accurately. \n\n" +
                        "To enable the movement notification, please ensure accelerator permissions are granted on your device. " +
                        "Otherwise it will not run."
            )
            .setPositiveButton("OK") { dialog, _->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
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