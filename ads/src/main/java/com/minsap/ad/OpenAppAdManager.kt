package com.minsap.ad

import android.app.Activity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAppAdManager @Inject constructor(
    private val admobAppOpenManager: AdmobAppOpenManager
) {

    val isShowingAdAppOpen: Boolean
        get() = admobAppOpenManager.isShowingAd

    private var minsapAdListener: WeakReference<MinsapAdListener>? = null

    fun initLoadAd(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        listener: MinsapAdListener
    ) {
        minsapAdListener = WeakReference(listener)

        val observer = object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                minsapAdListener = null
            }

            override fun onDestroy(owner: LifecycleOwner) {
                minsapAdListener = null
                lifecycleOwner.lifecycle.removeObserver(this)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        admobAppOpenManager.setLoadAdListener(minsapAdListener?.get() ?: return)
        admobAppOpenManager.loadAd(activity)
    }

    fun showAdIfAvailableAppOpen(activity: Activity, onShowAdCompleteListener: MinsapAdListener) {
        admobAppOpenManager.showAdIfAvailable(
            activity,
            onShowAdCompleteListener
        )
    }

    fun showAdIfAvailableAppOpen(activity: Activity) {
        admobAppOpenManager.showAdIfAvailable(activity)
    }

    fun clearResources() {
        admobAppOpenManager.clearResources()
    }

    fun clearAds() {
        admobAppOpenManager.clearAd()
    }

}
