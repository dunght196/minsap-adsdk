package com.minsap.ad

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.appopen.AppOpenAd
import java.lang.ref.WeakReference
import java.util.Date
import javax.inject.Inject

private const val TAG = "ADMOB_OPEN_APP_ADS"

class AdmobAppOpenManager @Inject constructor(
    private val configAdManager: ConfigAdManager
) {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false
    private var onLoadAdListener: WeakReference<MinsapAdListener>? = null

    fun setLoadAdListener(onLoadAdListener: MinsapAdListener) {
        this.onLoadAdListener = WeakReference(onLoadAdListener)
    }

    private var onShowAdCompleteCallback: WeakReference<MinsapAdListener>? = null
    var currentActivity: WeakReference<Activity>? = null

    /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
    private var loadTime: Long = 0

    /**
     * Load an ad.
     *
     * @param context the context of the activity that loads the ad
     */
    fun loadAd(context: Context) {
        // Do not load ad if there is an unused ad or one is already loading.
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdManagerAdRequest.Builder().build()
        AppOpenAd.load(
            context.applicationContext,
            configAdManager.getAdmodUnitId(),
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                /**
                 * Called when an app open ad has loaded.
                 *
                 * @param ad the loaded app open ad.
                 */
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    onLoadAdListener?.get()?.onLoadAdSuccess()
                    Log.d(TAG, "onAdLoaded.")
                }

                /**
                 * Called when an app open ad has failed to load.
                 *
                 * @param loadAdError the error.
                 */
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    onLoadAdListener?.get()?.onLoadAdFail()
                    Log.d(TAG, "onAdFailedToLoad: ${loadAdError.message}")
                }
            },
        )
    }

    /** Check if ad was loaded more than n hours ago. */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /** Check if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean {
        // Ad references in the app open beta will time out after four hours, but this time limit
        // may change in future beta versions. For details, see:
        // https://support.google.com/admob/answer/9341964?hl=en
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity the activity that shows the app open ad
     */
    fun showAdIfAvailable(activity: Activity) {
        showAdIfAvailable(
            activity,
            null
        )
    }

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: MinsapAdListener? = null) {
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            Log.d(TAG, "The app open ad is already showing.")
            return
        }
        currentActivity = WeakReference(activity)
        onShowAdCompleteCallback = WeakReference(onShowAdCompleteListener)

        // If the app open ad is not available yet, invoke the callback.
        if (!isAdAvailable()) {
            Log.d(TAG, "The app open ad is not ready yet.")
            onShowAdCompleteCallback?.get()?.onShowAdComplete()
            loadAd(currentActivity?.get() ?: return)
            return
        }

        Log.d(TAG, "Will show ad.")
        appOpenAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                /** Called when full screen content is dismissed. */
                override fun onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd = null
                    isShowingAd = false
                    Log.d(TAG, "onAdDismissedFullScreenContent.")
                    onShowAdCompleteCallback?.get()?.onShowAdComplete()
                    loadAd(currentActivity?.get() ?: return)
                }

                /** Called when fullscreen content failed to show. */
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAd = false
                    Log.d(TAG, "onAdFailedToShowFullScreenContent: ${adError.message}")
                    onShowAdCompleteCallback?.get()?.onShowAdComplete()
                    loadAd(currentActivity?.get() ?: return)
                }

                /** Called when fullscreen content is shown. */
                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "onAdShowedFullScreenContent.")
                }
            }
        isShowingAd = true
        appOpenAd?.show(currentActivity?.get() ?: return)
    }

    fun clearResources() {
        onLoadAdListener = null
        onShowAdCompleteCallback = null
        currentActivity = null
    }

    fun clearAd() {
        appOpenAd = null
    }
}