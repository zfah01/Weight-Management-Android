package com.example.weathertriggerapp2.locationHandler

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import kotlinx.coroutines.flow.Flow
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch


// https://www.youtube.com/watch?v=Jj14sw4Yxk0&list=LL&index=1
class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient // Get User Location
): LocationClient {
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            // Error handling - missing location permission
                if (!context.checkLocationPermission()) {
                    throw LocationClient.LocationException("Missing Location Permission")
                }

            // Ensure GPS and Network are enabled
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if(!gpsEnabled && !networkEnabled) {
                throw LocationClient.LocationException("GPS and Network is Disabled")
            }

            // create request
            val request = LocationRequest.create()
                .setInterval(interval)
                .setFastestInterval(interval)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)

            // callback to fetch new location
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    // get last fetched location
                    p0.locations.lastOrNull()?.let { location ->
                        // send location
                        launch { send(location) }
                    }
                }
            }

            // call client to location updates
            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }
}