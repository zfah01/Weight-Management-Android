package com.example.weathertriggerapp2.util

import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Calendar

/**
 * Function for rounding value to two decimal places
 * @param distance - distance walked
 * */
// https://stackoverflow.com/questions/49011924/round-double-to-1-decimal-place-kotlin-from-0-044999-to-0-1
fun roundOffDecimal(distance: Double?): Any {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(distance).toDouble()
}

/**
 * Function to return the current number of the week in the year
 * */
fun getCurrWeek(): Int {
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.MONDAY
    return calendar.get(Calendar.WEEK_OF_YEAR)
}