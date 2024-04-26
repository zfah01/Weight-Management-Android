package com.example.weathertriggerapp2


import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weathertriggerapp2.calorieValuesHandler.InsertTotalCalories
import com.example.weathertriggerapp2.data.HalfwayWorker
import com.example.weathertriggerapp2.notificationHandler.CalorieEndOfDayNotification
import com.example.weathertriggerapp2.notificationHandler.CalorieMidDayNotification
import com.example.weathertriggerapp2.notificationHandler.DistanceByAfternoon
import com.example.weathertriggerapp2.notificationHandler.RandomisedExerciseNotification
import com.example.weathertriggerapp2.notificationHandler.WeatherNotification
import com.example.weathertriggerapp2.notificationHandler.WeeklyEatingHabitsFeedbackNotification
import com.example.weathertriggerapp2.notificationHandler.WeeklyGoalsFeedbackNotification
import com.example.weathertriggerapp2.repository.CalorieCountRepository
import com.example.weathertriggerapp2.ui.theme.WeatherTriggerApp2Theme
import com.example.weathertriggerapp2.util.roundOffDecimal
import com.example.weathertriggerapp2.viewModel.MainScreen
import java.util.Calendar
import kotlin.math.sqrt


/**
 * Main Activity class for application
 * */
class MainActivity : ComponentActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null;

    private var running = false
    private var sensorPermission = false
    private var magnitudePrevious = 0.0
    private var stepCount = 0
    private var lastReset = Calendar.getInstance()

    private var stepGoal = 10
    private var goalIncreased = false


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    /**
     * function to measure steps user has taken via accelerometer sensor
     * */
    override fun onSensorChanged(event: SensorEvent?) {
        if(running && sensorPermission){
            val currDate = Calendar.getInstance();

            if(currDate.get(Calendar.YEAR) != lastReset.get(Calendar.YEAR)
                || currDate.get(Calendar.MONTH) != lastReset.get(Calendar.MONTH)
                || currDate.get(Calendar.DAY_OF_MONTH) != lastReset.get(Calendar.DAY_OF_MONTH)){
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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    /**
     * Create one time work request for steps
     * */
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

    /**
     * Setter functional for step count
     * */
    fun updateStepCount(stepValue: Int) {
        CalorieCountRepository.stepCount = stepValue
    }

    /**
     * Reset sensor values and new step goal
     * */
    private fun resetSteps(){
        stepGoal = stepCount
        goalIncreased = false
        CalorieCountRepository.goalSteps = stepCount
        CalorieCountRepository.goalIncreased = false
        stepCount = 0;
        lastReset = Calendar.getInstance();
    }


    /**
     * Function that checks if permissions are granted. If so, schedule notification worker
     * */
    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
                            Log.i("TAG", "scheduling location notification")
                            val alarmSchedulerWeather = WeatherNotification(applicationContext)
                            alarmSchedulerWeather.scheduleWeatherNotification()
                        }
                        else{
                            locationPermissionDeniedAlert()
                            val scheduleAlarm = RandomisedExerciseNotification(applicationContext)
                            scheduleAlarm.scheduleExerciseNotification()
                        }
                    }
                }
            }
        }
    }

    /**
     * Dialog alert if permissions associated with accelerator are denied
     * */
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

    /**
     * Dialog alert if permissions associated with location are denied
     * */
    private fun locationPermissionDeniedAlert() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Has Been Denied")
            .setMessage(
                "This app requires access to your location to provide weather data accurately. \n\n" +
                "To enable the weather notification, please ensure location permissions are granted on your device. " +
                "Otherwise it will not run. \n\nIn the meantime, a daily exercise suggestion will run!"
            )
            .setPositiveButton("OK") { dialog, _->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}