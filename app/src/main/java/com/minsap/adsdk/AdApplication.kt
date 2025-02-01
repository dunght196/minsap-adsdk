package com.minsap.adsdk

import android.app.Application
import com.minsap.ad.MinsapAdManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AdApplication : Application() {
    @Inject
    lateinit var minsapAdManager: MinsapAdManager

    override fun onCreate() {
        super.onCreate()
        minsapAdManager.initializeConfig(unitId = "/21775744923/example/app-open", testDevices = listOf("ABCDEF012345"))
    }
}