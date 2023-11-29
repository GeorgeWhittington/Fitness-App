package com.foxden.fitnessapp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

class LocationService(context: Context) {
    private val ctx: Context
    private val locationProvider: FusedLocationProviderClient
    private val locationManager: LocationManager
    private val locationRequest: LocationRequest

    init {
        ctx = context
        locationProvider = LocationServices.getFusedLocationProviderClient(ctx)
        locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
    }

    /**
     * Confirms that the user has GPS turned on.
     */
    private fun verifyGPS() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            throw LocationServiceException.LocationDisabledException()

        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            throw LocationServiceException.NoNetworkEnabledException()
    }

    /**
     * Confirms that the user has provided fine or course location permissions.
     */
    private fun verifyGPSPermissions() {
        fun checkPermission(permission: String): Boolean {
            return ActivityCompat.checkSelfPermission(
                ctx, permission
            ) != PackageManager.PERMISSION_GRANTED
        }

        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) throw LocationServiceException.MissingPermissionException()
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastLocation() : Location? {
        verifyGPS()

        runCatching {
            verifyGPSPermissions()
            return locationProvider.getLastLocation().await()
        }.getOrElse {
            throw LocationServiceException.UnknownException(stace = it.stackTraceToString())
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(callback: LocationCallback) {
        verifyGPS()
        verifyGPSPermissions()
        locationProvider.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper())
    }

    fun stopLocationUpdates(callback: LocationCallback) {
        locationProvider.removeLocationUpdates(callback)
    }

    sealed class LocationServiceException : Exception() {
        class MissingPermissionException : LocationServiceException()
        class LocationDisabledException : LocationServiceException()
        class NoNetworkEnabledException : LocationServiceException()
        class UnknownException(val stace: String) : LocationServiceException()
    }
}