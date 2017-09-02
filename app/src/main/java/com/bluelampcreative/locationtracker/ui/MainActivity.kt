package com.bluelampcreative.locationtracker.ui

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bluelampcreative.locationtracker.R
import com.google.android.gms.location.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.joda.time.DateTime
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber
import java.io.IOException

@RuntimePermissions(kotlin = true)
class MainActivity : AppCompatActivity(), Callback {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    lateinit var countDownTimer: CountDownTimer

    val url = "https://demo0280857.mockable.io/locationdata"
    val JSON = MediaType.parse("application/json; charset=utf-8")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationCallback = createLocationCallback()
        countDownTimer = createCountDownTimer()

        btnLocationTracking.setOnClickListener {
            toggleLocationTracking()
        }
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun createLocationClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationTracking()
    }

    private fun createLocationCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.locations?.forEach { location -> postLocation(location) }
            }
        }
    }

    private fun createCountDownTimer(): CountDownTimer {
        return object : CountDownTimer(15000, 1000) {
            override fun onFinish() {
                countDownTimer.cancel()
                txtSecondsUntilUpdate.text = "--"
            }

            override fun onTick(millsToFinish: Long) {
                txtSecondsUntilUpdate.text = "${(millsToFinish / 1000)} seconds"
            }
        }
    }

    private fun toggleLocationTracking() {
        if (linTextContainer.visibility == View.VISIBLE) {
            stopLocationTracking()
        } else {
            createLocationClientWithCheck()
        }
    }

    private fun postLocation(location: Location?) {

        val okHttpClient = OkHttpClient()
        val body = Gson().toJson(location)
        val requestBody = RequestBody.create(JSON, body)
        val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

        okHttpClient.newCall(request).enqueue(this)

        countDownTimer.start()

        txtLocationUpdateList.text = "${txtLocationUpdateList.text}\n\n  - ${location.toString()}"
        scrlLogContainer.fullScroll(View.FOCUS_DOWN)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationTracking() {
        txtLocationUpdateList.text = "${txtLocationUpdateList.text}\n\nBEGINNING LOCATION TRACKING : ${DateTime.now()}"
        linTextContainer.visibility = View.VISIBLE
        btnLocationTracking.text = getString(R.string.end_location_tracking)


        fusedLocationProviderClient.requestLocationUpdates(getLocationRequest(), locationCallback, null)
    }

    private fun stopLocationTracking() {
        txtLocationUpdateList.text = "${txtLocationUpdateList.text}\n\nENDING LOCATION TRACKING : ${DateTime.now()}"
        linTextContainer.visibility = View.GONE
        btnLocationTracking.text = getString(R.string.begin_location_tracking)
        countDownTimer.cancel()

        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun getLocationRequest(): LocationRequest? {
        return LocationRequest
                .create()
                .setInterval(15000)
                .setFastestInterval(15000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    override fun onFailure(call: Call?, e: IOException?) {
        Timber.e("Failed to post location")
        call?.cancel()
    }

    override fun onResponse(call: Call?, response: Response?) {
        Timber.e("Response %s ", response?.body()?.string())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

}
