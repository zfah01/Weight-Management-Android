package com.example.weathertriggerapp2

import com.example.weathertriggerapp2.broadcast.Notification
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

class NotificationMessagesTests {
    @Test
    fun calorieEndOfDayMessageMaleMetTargets() {
        val receiver = Notification()
        val message = receiver.getEndOfDayNotificationMessage(2000.0, "Male")
        assertEquals(message, "You have consumed 2000.0 calories today and met your daily calorie target! Well done!")
    }

    @Test
    fun calorieEndOfDayMessageMaleNotMetTargets() {
        val receiver = Notification()
        val message = receiver.getEndOfDayNotificationMessage(2500.0, "Male")
        assertEquals(message, "You have consumed 2500.0 calories today! \n" +
                "Unfortunately, you didn't quite meet your calorie target for the day. Remember, maintaining a balanced diet is crucial for your overall well-being!"
        )
    }
    @Test
    fun calorieEndOfDayMessageFemaleMetTargets() {
        val receiver = Notification()
        val message = receiver.getEndOfDayNotificationMessage(1500.0, "Female")
        assertEquals(message, "You have consumed 1500.0 calories today and met your daily calorie target! Well done!")
    }

    @Test
    fun calorieEndOfDayMessageFemaleNotMetTargets() {
        val receiver = Notification()
        val message = receiver.getEndOfDayNotificationMessage(2000.0, "Female")
        assertEquals(message, "You have consumed 2000.0 calories today! \n" +
                "Unfortunately, you didn't quite meet your calorie target for the day. Remember, maintaining a balanced diet is crucial for your overall well-being!"
        )
    }

    @Test
    fun weatherMessageClearWeather() {
        val receiver = Notification()
        val message = receiver.getWeatherNotificationMessage("clear")
        val arr = receiver.outdoorActivities
        assertTrue(arr.any { message.contains(it) })
    }

    @Test
    fun weatherMessagePoorWeather() {
        val receiver = Notification()
        val message = receiver.getWeatherNotificationMessage("snow")
        val arr = receiver.indoorActivities
        assertTrue(arr.any { message.contains(it) })
    }

    @Test
    fun weatherMessageNoResponse() {
        val receiver = Notification()
        val message = receiver.getWeatherNotificationMessage("no response")
        val arr = receiver.indoorActivities
        assertTrue(arr.any { message.contains(it) })
    }

    @Test
    fun sugarExceededMessage() {
        val receiver = Notification()
        val message = receiver.getSugarAndFatNotificationMessage(34.0, 23.0)
        assertEquals(message, "You have exceeded your daily sugar limit of 30g by 4 last week. Try opting for water instead of fizzy drink or having less sugar in your daily cups of tea or coffee!"
        )
    }

    @Test
    fun satFatExceededMessage() {
        val receiver = Notification()
        val message = receiver.getSugarAndFatNotificationMessage(23.0, 53.0)
        assertEquals(message, "You have exceeded your daily saturated fats limit by 23 last week. Try opting for less fatty foods, such as fatty meats or butter to improve heart health."
        )
    }

    @Test
    fun satFatAndSugarExceededMessage() {
        val receiver = Notification()
        val message = receiver.getSugarAndFatNotificationMessage(33.0, 53.0)
        assertEquals(message,
             "You have exceeded your daily saturated fats limit by 23 last week. Try opting for less fatty foods, such as fatty meats or butter to improve heart health."
                     + "\n" +"You have exceeded your daily sugar limit of 30g by 3 last week. Try opting for water instead of fizzy drink or having less sugar in your daily cups of tea or coffee!"
        )
    }

    @Test
    fun weeklyFeedbackMessageMaleMetTargets() {
        val receiver = Notification()
        val message = receiver.getWeeklyFeedbackMessage(2000.0, 5000.0, "Male")
        assertEquals(message, "Well Done! You have consumed a healthy average of 2000 calories each day this week." +
        "\n" + "Congratulations! You have hit an average of 3 steps per day! Keep it up!")
    }

    @Test
    fun weeklyFeedbackMessageFemaleMetTargets() {
        val receiver = Notification()
        val message = receiver.getWeeklyFeedbackMessage(1500.0, 5000.0, "Female")
        assertEquals(message, "Well Done! You have consumed a healthy average of 1500 calories each day this week." +
                "\n" + "Congratulations! You have hit an average of 3 steps per day! Keep it up!")
    }

    @Test
    fun weeklyFeedbackMessageMaleMetCalorieTargets() {
        val receiver = Notification()
        val message = receiver.getWeeklyFeedbackMessage(2000.0, 2000.0, "Male")
        assertEquals(message, "Well Done! You have consumed a healthy average of 2000 calories each day this week." +
                "\n" + "You have hit an average of 1 steps per day! Why don't you try going for another kilometer?")
    }

    @Test
    fun weeklyFeedbackMessageFemaleMetCalorieTargets() {
        val receiver = Notification()
        val message = receiver.getWeeklyFeedbackMessage(1500.0, 2000.0, "Female")
        assertEquals(message, "Well Done! You have consumed a healthy average of 1500 calories each day this week." +
                "\n" + "You have hit an average of 1 steps per day! Why don't you try going for another kilometer?")
    }

    @Test
    fun weeklyFeedbackMessageMaleMetStepTargets() {
        val receiver = Notification()
        val message = receiver.getWeeklyFeedbackMessage(2500.0, 5000.0, "Male")
        assertEquals(message, "You haven't met your daily calorie intake this week! You consumed an average of 2500 calories. Remember a healthy daily caloric is between 1900.0 - 2100.0 calories if you are aiming for healthy weight loss." +
                "\n" + "Congratulations! You have hit an average of 3 steps per day! Keep it up!")
    }

    @Test
    fun weeklyFeedbackMessageFemaleMetStepTargets() {
        val receiver = Notification()
        val message = receiver.getWeeklyFeedbackMessage(2000.0, 5000.0, "Female")
        assertEquals(message,
            "You haven't met your daily calorie intake this week! You consumed an average of 2000 calories. Remember a healthy daily caloric is between 1400.0 - 1600.0 calories if you are aiming for healthy weight loss." +
                "\n" + "Congratulations! You have hit an average of 3 steps per day! Keep it up!")
    }

}