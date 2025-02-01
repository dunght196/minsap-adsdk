package com.minsap.ad

import javax.inject.Singleton

@Singleton
class ConfigAdManager {
    private var admobUnitId = ""
    private var testDevicesId = listOf<String>()

    fun setAdmodUnitId(id: String) {
        this.admobUnitId = id
    }

    fun getAdmodUnitId() = admobUnitId

    fun setTestDeviceIds(devices: List<String>) {
        this.testDevicesId = devices
    }

    fun getTestDeviceIds() = testDevicesId
}