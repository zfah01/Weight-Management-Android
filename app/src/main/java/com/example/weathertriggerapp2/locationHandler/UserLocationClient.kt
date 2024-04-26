package com.example.weathertriggerapp2.locationHandler


import android.location.Location
import kotlinx.coroutines.flow.Flow

// https://www.youtube.com/watch?v=Jj14sw4Yxk0&list=LL&index=1

/**
 * Interface representing the User Location Client for fetching location updates
 * */
interface UserLocationClient {
    // fetches new location
    fun getLocationUpdates(interval: Long): Flow<Location>

    // if user does not have location permission, gps disabled, etc.
    class LocationException(message:String): Exception()
}