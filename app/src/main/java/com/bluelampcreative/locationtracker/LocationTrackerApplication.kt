package com.bluelampcreative.locationtracker

import android.app.Application
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber


class LocationTrackerApplication : Application() {


    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        JodaTimeAndroid.init(this)
    }
}