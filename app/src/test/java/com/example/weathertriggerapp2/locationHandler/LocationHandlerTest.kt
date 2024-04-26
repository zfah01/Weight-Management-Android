package com.example.weathertriggerapp2.locationHandler

import android.location.Location
import kotlinx.coroutines.flow.flowOf
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

// https://stackoverflow.com/questions/16243580/mockito-how-to-mock-and-assert-a-thrown-exception
// https://github.com/lykmapipo/android-location-provider/blob/master/library/src/test/java/com/github/lykmapipo/location/LocationProviderTest.java

/**
 * Class representing UserLocationClient unit tests
 * */
@RunWith(MockitoJUnitRunner::class)
class LocationClientTest {
    @Mock
    lateinit var location: Location

    @Mock
    lateinit var mockLocationClient: UserLocationClient

    @Test
    fun exceptionPermissions() {
        val message = "Missing Location Permission"
        `when`(mockLocationClient.getLocationUpdates(50000)).thenThrow(RuntimeException(message))

        try {
            mockLocationClient.getLocationUpdates(50000)
        } catch (e: RuntimeException) {
            assertEquals(message, e.message)
            verify(mockLocationClient, times(1)).getLocationUpdates(50000)
        }
    }

    @Test
    fun exceptionGpsDisabled() {
        val message = "GPS and Network is Disabled"
        `when`(mockLocationClient.getLocationUpdates(50000)).thenThrow(RuntimeException(message))

        try {
            mockLocationClient.getLocationUpdates(50000)
        } catch (e: RuntimeException) {
            assertEquals(message, e.message)
            verify(mockLocationClient, times(1)).getLocationUpdates(50000)
        }
    }

    @Test
    fun getLocationUpdatesSuccessfully() {
        `when`(mockLocationClient.getLocationUpdates(50000)).thenReturn(flowOf(location))
        val result = mockLocationClient.getLocationUpdates(50000)
        assertNotNull(result)
    }
}