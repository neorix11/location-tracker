package com.bluelampcreative.locationtracker.ui

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bluelampcreative.locationtracker.R
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber

@RuntimePermissions(kotlin = true)
class MainActivity : AppCompatActivity() {


    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createLocationCallback()

        btnLocationTracking.setOnClickListener {
            toggleLocationTracking()
        }
    }

    private fun toggleLocationTracking() {
        if (linTextContainer.visibility == View.VISIBLE) {
            stopLocationTracking()

        } else {
            createLocationClientWithCheck()
        }
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun createLocationClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationTracking()
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.locations?.forEach { location -> postLocation(location) }
            }
        }
    }

    private fun getLocationRequest(): LocationRequest? {
        return LocationRequest
                .create()
                .setInterval(15000)
                .setFastestInterval(15000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }


    private fun postLocation(location: Location?) {
        //replace with a web call
        Timber.e("New Location %s", location)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationTracking() {
        Timber.d("Beginning Location Tracking")
        linTextContainer.visibility = View.VISIBLE
        btnLocationTracking.text = getString(R.string.end_location_tracking)

        fusedLocationProviderClient.requestLocationUpdates(getLocationRequest(), locationCallback, null)
    }

    private fun stopLocationTracking() {
        Timber.d("Ending Location Tracking")
        linTextContainer.visibility = View.GONE
        btnLocationTracking.text = getString(R.string.begin_location_tracking)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

}
