package com.example.stepcounter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.example.stepcounter.Data.HalfwayWorker
import com.example.stepcounter.Data.WorkRequest
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null;

    private var running = false
    private var MagnitudePrevious = 0.0
    private var stepCount = 0

    private var currentMovement = 0
    private var totalMovement = 0
    private var previousTotalMovement = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
                Surface() {
                    MovementTrackerLayout(stepCount)
                }
        }

//        loadData()
//        resetSteps()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
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

    override fun onSensorChanged(event: SensorEvent?) {
//        val request = OneTimeWorkRequestBuilder<WorkRequest>().build()


        if(running){
            var xacceleration = event?.values?.get(0)
            var yacceleration = event?.values?.get(1)
            var zacceleration = event?.values?.get(2)

            var magnitude = Math.sqrt((xacceleration!! *xacceleration + yacceleration!! *yacceleration + zacceleration!! *zacceleration).toDouble())

            var magnitudeDelta = magnitude - MagnitudePrevious

            MagnitudePrevious = magnitude

            if(magnitudeDelta > 6){
                stepCount++
                if(stepCount == 3){
                    createWorkRequest("You've completed 50% of you step target!")
                }
                else if(stepCount == 6){
                    createWorkRequest("You've hit your step target today! Go you!")
                }
            }
        }
        setContent{
            Surface() {
                MovementTrackerLayout(stepCount)
            }
        }
    }

    private fun createWorkRequest(message: String) {
        val myWorkRequest = OneTimeWorkRequestBuilder<HalfwayWorker>()
            .setInputData(workDataOf(
                "title" to "Daily Steps",
                "message" to message,
            )
            )
            .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    fun resetSteps(){
        previousTotalMovement = totalMovement
        currentMovement = 0

    }

    private fun saveData(){
//        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
//
//        val editor = sharedPreferences.edit()
//        editor.putFloat("key1", previousTotalSteps)
//        editor.apply()
//    }
//
//    private fun loadData(){
//        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
//        val savedNumber = sharedPreferences.getFloat("key1", 0f)
//
//        Log.d("MainActivity", "$savedNumber")
//        previousTotalSteps = savedNumber
    }
}

@Composable
fun MovementTrackerLayout(currentMovement: Int){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = "Steps so far:"
        )
        Text(
            text = (currentMovement.toString())
        )
    }
}