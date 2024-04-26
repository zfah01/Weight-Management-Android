package com.example.weathertriggerapp2.util

import org.junit.Test
import java.util.Calendar
import kotlin.test.assertEquals


/**
 * Class representing Constants unit tests
 * */
class ConstantsTests {
    @Test
    fun roundDistanceValue() {
        val distance = roundOffDecimal(2.34444444)
        assertEquals(distance, 2.35)
    }

    @Test
    fun getCurrentWeek() {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        val actualWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val week = getCurrWeek()
        assertEquals(week, actualWeek)
    }
}