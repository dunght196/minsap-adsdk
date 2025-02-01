package com.minsap.ad

interface MinsapAdListener {
    fun onLoadAdSuccess()

    fun onLoadAdFail()

    fun onShowAdComplete()
}