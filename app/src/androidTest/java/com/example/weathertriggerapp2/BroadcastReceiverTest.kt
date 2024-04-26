package com.example.weathertriggerapp2

import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import com.example.weathertriggerapp2.broadcast.Notification
import com.example.weathertriggerapp2.broadcast.Notification.ActionAlarms.ACTION_ALARM_1
import com.example.weathertriggerapp2.broadcast.Notification.ActionAlarms.ACTION_ALARM_2
import com.example.weathertriggerapp2.broadcast.Notification.ActionAlarms.ACTION_ALARM_3
import com.example.weathertriggerapp2.broadcast.Notification.ActionAlarms.ACTION_ALARM_4
import com.example.weathertriggerapp2.broadcast.Notification.ActionAlarms.ACTION_ALARM_5
import com.example.weathertriggerapp2.broadcast.Notification.ActionAlarms.ACTION_ALARM_6
import com.example.weathertriggerapp2.broadcast.Notification.ActionAlarms.ACTION_ALARM_7
import com.example.weathertriggerapp2.broadcast.Notification.ActionAlarms.ACTION_ALARM_8
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Class representing Notification unit tests
 * */
class BroadcastReceiverTest {

    @Test
    fun middayCalorieNotificationIsCreatedInCorrectIntent() {
        val intent = Intent(ACTION_ALARM_1)
        val receiver = Notification()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        assertFalse(receiver.middayCalorie)

        receiver.onReceive(appContext, intent)

        assertTrue(receiver.middayCalorie)
    }

    @Test
    fun eodCalorieNotificationIsCreatedInCorrectIntent() {
        val intent = Intent(ACTION_ALARM_2)
        val receiver = Notification()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        assertFalse(receiver.endOfDayCalorie)

        receiver.onReceive(appContext, intent)

        assertTrue(receiver.endOfDayCalorie)
    }

    @Test
    fun weatherNotificationIsCreatedInCorrectIntent() {
        val intent = Intent(ACTION_ALARM_3)
        val receiver = Notification()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        assertFalse(receiver.weatherNotification)

        receiver.onReceive(appContext, intent)

        assertTrue(receiver.weatherNotification)
    }

    @Test
    fun calorieInsertIsCreatedInCorrectIntent() {
        val intent = Intent(ACTION_ALARM_4)
        val receiver = Notification()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        assertFalse(receiver.insertCalories)

        receiver.onReceive(appContext, intent)

        assertTrue(receiver.insertCalories)
    }

    @Test
    fun weeklyGoalFeedbackNotificationIsCreatedInCorrectIntent() {
        val intent = Intent(ACTION_ALARM_5)
        val receiver = Notification()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        assertFalse(receiver.weeklyGoals)

        receiver.onReceive(appContext, intent)

        assertTrue(receiver.weeklyGoals)
    }

    @Test
    fun weeklyEatingHabitsNotificationIsCreatedInCorrectIntent() {
        val intent = Intent(ACTION_ALARM_6)
        val receiver = Notification()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        assertFalse(receiver.weeklyEatingHabits)

        receiver.onReceive(appContext, intent)

        assertTrue(receiver.weeklyEatingHabits)
    }

    @Test
    fun distanceTargetNotificationIsCreatedInCorrectIntent() {
        val intent = Intent(ACTION_ALARM_7)
        val receiver = Notification()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        assertFalse(receiver.distanceTarget)

        receiver.onReceive(appContext, intent)

        assertTrue(receiver.distanceTarget)
    }

    @Test
    fun exerciseNotificationIsCreatedInCorrectIntent() {
        val intent = Intent(ACTION_ALARM_8)
        val receiver = Notification()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        assertFalse(receiver.randomExercise)

        receiver.onReceive(appContext, intent)

        assertTrue(receiver.randomExercise)
    }
}