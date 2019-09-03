package com.example.oliverboardtest.ui.baseModel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

open class LocationHelper(val context: Context, val listener: LocationResultCallback?) {

    private var callerId: Int = 0
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback
    private var locationManager: LocationManager

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                val location = locationResult.locations[0]
                listener?.onLocationResult(location, callerId)
            }
        }
    }

    fun createLocationRequest(): LocationRequest {
        return LocationRequest().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            smallestDisplacement = 10f
        }
    }


    fun startLocationUpdates(callerId: Int) {
        this.callerId = callerId
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient?.requestLocationUpdates(
            createLocationRequest(),
            locationCallback,
            null
        )
    }

    fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}