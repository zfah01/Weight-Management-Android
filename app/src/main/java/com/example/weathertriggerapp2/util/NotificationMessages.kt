package com.example.weathertriggerapp2.util

import java.util.Locale

/**
 * Array of outdoor activities
 * */
val outdoorActivities = arrayOf("walk", "run", "yoga session")

/**
 * Array of indoor activities
 * */
val indoorActivities = arrayOf("burpees", "push-ups", "arm extensions", "tummy twists")

/**
 * Function for building the end of day notification message based on recorded calorie amount and gender
 * @param caloriesAmount - total calorie intake
 * @param gender - user inputted gender
 * */
fun getEndOfDayNotificationMessage(caloriesAmount: Double, gender: String): String {
    val minCalorieRange : Double = if(gender == "Male") {
        1900.0
    } else {
        1400.0
    }
    val maxCalorieRange : Double = if(gender == "Male") {
        2100.0
    } else {
        1600.0
    }
    return if(caloriesAmount == 0.0) {
        "Have you added your calorie intake for today yet? Remember to record it accurately track your progress!"
    }
    else if ((caloriesAmount in minCalorieRange..maxCalorieRange)) {
        "You have consumed $caloriesAmount calories today and met your daily calorie target! Well done!"
    } else {
        "You have consumed $caloriesAmount calories today! \n" +
                "Unfortunately, you didn't quite meet your calorie target of $minCalorieRange - $maxCalorieRange for the day. Remember, maintaining a balanced diet is crucial for your overall well-being!"
    }
}


/**
 * Function for building the weather notification message based on weather main value
 * @param weather - weather type
 * */
fun getWeatherNotificationMessage(weather: String): String {
    return when (weather.lowercase(Locale.getDefault())) {
        "clear" -> "Weather near you is clear today! Do you have time for a ${outdoorActivities.randomOrNull()} today?"
        "no response" -> "Hmm, something went wrong with fetching your location. In the meantime, why don't you try some indoor exercises today like ${indoorActivities.randomOrNull()} or ${indoorActivities.randomOrNull()}?"
        else -> "Weather near you seems to be poor today. Why don't you try some indoor exercises today like ${indoorActivities.randomOrNull()} or ${indoorActivities.randomOrNull()} today?"
    }
}

/**
 * Function for building the sugar and saturated fats notification based on daily average sugar and saturated fats intake
 * @param dailyFat - average saturated fats intake
 * @param dailySugar - average sugar intake
 * */
fun getSugarAndFatNotificationMessage(dailySugar: Double, dailyFat : Double): String {
    val exceededSugarIntake = dailySugar - 30.0
    val exceededFatIntake = dailyFat - 30.0
    var sugarMessage = ""
    var fatMessage = ""
    if(dailySugar > 30.0) {
        sugarMessage = "You have exceeded your daily sugar limit of 30g by ${exceededSugarIntake.toInt()} last week. Try opting for water instead of fizzy drink or having less sugar in your daily cups of tea or coffee!"
    }
    if(dailyFat > 30.0) {
        fatMessage = "You have exceeded your daily saturated fats limit by ${exceededFatIntake.toInt()} last week. Try opting for less fatty foods, such as fatty meats or butter to improve heart health."
    }

    return if(sugarMessage.isEmpty() && fatMessage.isNotEmpty()) {
        fatMessage
    }
    else if (sugarMessage.isNotEmpty() && fatMessage.isEmpty()) {
        sugarMessage
    }
    else {
        fatMessage + "\n" + sugarMessage
    }
}

/**
 * Function for building the weekly feedback notification message based on average calorie intake, steps taken and user gender
 * @param caloriesAmount - average calorie intake
 * @param weeklySteps - average steps taken
 * @param gender - user inputted gender
 * */
fun getWeeklyFeedbackMessage(caloriesAmount: Double, weeklySteps : Double, gender: String): String {
    if(!caloriesAmount.isNaN() || !weeklySteps.isNaN()) {
        val currDistance = (weeklySteps.times(0.00074))
        val minCalorieRange: Double = if (gender == "Male") {
            1900.0
        } else {
            1400.0
        }
        val maxCalorieRange: Double = if (gender == "Male") {
            2100.0
        } else {
            1600.0
        }
        val foodHabitsMessage: String =
            if (caloriesAmount in minCalorieRange..maxCalorieRange) {
                "Well Done! You have consumed a healthy average of ${caloriesAmount.toInt()} calories each day this week."

            } else {
                "You haven't met your daily calorie intake this week! You consumed an average of ${caloriesAmount.toInt()} calories. " +
                        "Remember a healthy daily caloric is between $minCalorieRange - $maxCalorieRange calories if you are aiming for healthy weight loss. \n"
//                                "For a more accurate daily caloric intake, you are able to calculate this via: https://www.calculator.net/calorie-calculator.html"
            }

        val activityMessage: String = if (currDistance!! >= 2.5) {
            "Congratulations! You have hit an average of ${currDistance.toInt()} km per day! Keep it up!"
        } else {
            "You have hit an average of ${currDistance.toInt()} km per day! Why don't you try going for another kilometer?"
        }


        return foodHabitsMessage + "\n" + activityMessage
    }
    else {
        return "Seems like we don't have enough data yet! Keep up with the good work to build your profile!"
    }
}

/**
 * Function for building the distance covered notification message based on steps taken and target steps goal
 * @param stepCount - steps recorded
 * @param goalSteps - target step goal
 * */
fun getDistanceNotificationMessage(stepCount: Int, goalSteps: Int?): String {
    val currDistance = (stepCount.times(0.00074))
    val currDistanceRounded = roundOffDecimal(currDistance)
    if (goalSteps != null) {
        val goalDistance = (goalSteps.times(0.00074))
        return if(goalSteps > 0) {
            val difference = goalDistance - currDistance!!
            val differenceRounded = roundOffDecimal(difference)
            "You are $differenceRounded km short of meeting yesterday's distance. Let's try and walk another kilometer!"
        } else{
            "You've walked $currDistanceRounded km today! Let's keep going and walk another kilometer!"
        }
    }
    return "You've walked $currDistanceRounded km today! Let's keep going and walk another kilometer!"
}
