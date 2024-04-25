package com.example.weathertriggerapp2

import com.example.weathertriggerapp2.util.getDistanceNotificationMessage
import com.example.weathertriggerapp2.util.getEndOfDayNotificationMessage
import com.example.weathertriggerapp2.util.getSugarAndFatNotificationMessage
import com.example.weathertriggerapp2.util.getWeatherNotificationMessage
import com.example.weathertriggerapp2.util.getWeeklyFeedbackMessage
import com.example.weathertriggerapp2.util.indoorActivities
import com.example.weathertriggerapp2.util.outdoorActivities
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationMessagesTests {
    @Test
    fun calorieEndOfDayMessageCaloriesNotRecorded() {
        val message = getEndOfDayNotificationMessage(0.0, "Male")
        assertEquals(message, "Have you added your calorie intake for today yet? Remember to record it accurately track your progress!")
    }
    @Test
    fun calorieEndOfDayMessageMaleMetTargets() {
        val message = getEndOfDayNotificationMessage(2000.0, "Male")
        assertEquals(message, "You have consumed 2000.0 calories today and met your daily calorie target! Well done!")
    }

    @Test
    fun calorieEndOfDayMessageMaleNotMetTargets() {
        val message = getEndOfDayNotificationMessage(2500.0, "Male")
        assertEquals(message, "You have consumed 2500.0 calories today! \n" +
                "Unfortunately, you didn't quite meet your calorie target for the day. Remember, maintaining a balanced diet is crucial for your overall well-being!"
        )
    }
    @Test
    fun calorieEndOfDayMessageFemaleMetTargets() {
        val message = getEndOfDayNotificationMessage(1500.0, "Female")
        assertEquals(message, "You have consumed 1500.0 calories today and met your daily calorie target! Well done!")
    }

    @Test
    fun calorieEndOfDayMessageFemaleNotMetTargets() {
        val message = getEndOfDayNotificationMessage(2000.0, "Female")
        assertEquals(message, "You have consumed 2000.0 calories today! \n" +
                "Unfortunately, you didn't quite meet your calorie target for the day. Remember, maintaining a balanced diet is crucial for your overall well-being!"
        )
    }

    @Test
    fun weatherMessageClearWeather() {
        val message = getWeatherNotificationMessage("clear")
        val arr = outdoorActivities
        assertTrue(arr.any { message.contains(it) })
    }

    @Test
    fun weatherMessagePoorWeather() {
        val message = getWeatherNotificationMessage("snow")
        val arr = indoorActivities
        assertTrue(arr.any { message.contains(it) })
    }

    @Test
    fun weatherMessageNoResponse() {
        val message = getWeatherNotificationMessage("no response")
        val arr = indoorActivities
        assertTrue(arr.any { message.contains(it) })
    }

    @Test
    fun sugarExceededMessage() {
        val message = getSugarAndFatNotificationMessage(34.0, 23.0)
        assertEquals(message, "You have exceeded your daily sugar limit of 30g by 4 last week. Try opting for water instead of fizzy drink or having less sugar in your daily cups of tea or coffee!"
        )
    }

    @Test
    fun satFatExceededMessage() {
        val message = getSugarAndFatNotificationMessage(23.0, 53.0)
        assertEquals(message, "You have exceeded your daily saturated fats limit by 23 last week. Try opting for less fatty foods, such as fatty meats or butter to improve heart health."
        )
    }

    @Test
    fun satFatAndSugarExceededMessage() {
        val message = getSugarAndFatNotificationMessage(33.0, 53.0)
        assertEquals(message,
             "You have exceeded your daily saturated fats limit by 23 last week. Try opting for less fatty foods, such as fatty meats or butter to improve heart health."
                     + "\n" +"You have exceeded your daily sugar limit of 30g by 3 last week. Try opting for water instead of fizzy drink or having less sugar in your daily cups of tea or coffee!"
        )
    }

    @Test
    fun weeklyFeedbackMessageMaleMetTargets() {
        val message = getWeeklyFeedbackMessage(2000.0, 5000.0, "Male")
        assertEquals(message, "Well Done! You have consumed a healthy average of 2000 calories each day this week." +
        "\n" + "Congratulations! You have hit an average of 3 steps per day! Keep it up!")
    }

    @Test
    fun weeklyFeedbackMessageFemaleMetTargets() {
        val message = getWeeklyFeedbackMessage(1500.0, 5000.0, "Female")
        assertEquals(message, "Well Done! You have consumed a healthy average of 1500 calories each day this week." +
                "\n" + "Congratulations! You have hit an average of 3 steps per day! Keep it up!")
    }

    @Test
    fun weeklyFeedbackMessageMaleMetCalorieTargets() {
        val message = getWeeklyFeedbackMessage(2000.0, 2000.0, "Male")
        assertEquals(message, "Well Done! You have consumed a healthy average of 2000 calories each day this week." +
                "\n" + "You have hit an average of 1 steps per day! Why don't you try going for another kilometer?")
    }

    @Test
    fun weeklyFeedbackMessageFemaleMetCalorieTargets() {
        val message = getWeeklyFeedbackMessage(1500.0, 2000.0, "Female")
        assertEquals(message, "Well Done! You have consumed a healthy average of 1500 calories each day this week." +
                "\n" + "You have hit an average of 1 steps per day! Why don't you try going for another kilometer?")
    }

    @Test
    fun weeklyFeedbackMessageMaleMetStepTargets() {
        val message = getWeeklyFeedbackMessage(2500.0, 5000.0, "Male")
        assertEquals(message, "You haven't met your daily calorie intake this week! You consumed an average of 2500 calories. Remember a healthy daily caloric is between 1900.0 - 2100.0 calories if you are aiming for healthy weight loss. " +
                "\n\n" + "Congratulations! You have hit an average of 3 steps per day! Keep it up!")
    }

    @Test
    fun weeklyFeedbackMessageFemaleMetStepTargets() {
        val message = getWeeklyFeedbackMessage(2000.0, 5000.0, "Female")
        assertEquals(message,
            "You haven't met your daily calorie intake this week! You consumed an average of 2000 calories. Remember a healthy daily caloric is between 1400.0 - 1600.0 calories if you are aiming for healthy weight loss. " +
                "\n\n" + "Congratulations! You have hit an average of 3 steps per day! Keep it up!")
    }

    @Test
    fun distanceMessageGoalNotMet() {
        val message = getDistanceNotificationMessage(2000, 4000)
        assertEquals(message, "You are 1.48 km short of meeting yesterday's distance. Let's try and walk another kilometer!")
    }

    @Test
    fun distanceGoalMet() {
        val message= getDistanceNotificationMessage(5000, 0)
        assertEquals(message, "You've walked 3.7 km today! Let's keep going and walk another kilometer!")
    }

    @Test
    fun distanceGoalNull() { 
        val message = getDistanceNotificationMessage(5000, null)
        assertEquals(message, "You've walked 3.7 km today! Let's keep going and walk another kilometer!")
    }
}