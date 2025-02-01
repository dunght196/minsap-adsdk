package com.minsap.ad

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.minsap.ad.di.GGCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MinsapAdManager @Inject constructor(
    private val coroutineDispatcher: GGCoroutineDispatchers,
    private val configAdManager: ConfigAdManager,
    private val openAppAdManager: OpenAppAdManager,
) {

    fun initializeConfig(unitId: String, testDevices: List<String>) {
        configAdManager.run {
            setAdmodUnitId(unitId)
            setTestDeviceIds(testDevices)
        }
    }

    fun initializeAds(context: Context, coroutineScope: CoroutineScope) {
        // Set your test devices.
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(configAdManager.getTestDeviceIds())
                .build()
        )

        coroutineScope.launch(coroutineDispatcher.io) {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(context.applicationContext) {}
        }
    }

    fun clearResources() {
        openAppAdManager.clearResources()
    }

    fun clearAds() {
        openAppAdManager.clearAds()
    }

}