package com.minsap.adsdk

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.minsap.ad.MinsapAdListener
import com.minsap.ad.MinsapAdManager
import com.minsap.ad.OpenAppAdManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MinsapAdListener {
    @Inject
    lateinit var minsapAdManager: MinsapAdManager
    @Inject
    lateinit var openAppAdManager: OpenAppAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        minsapAdManager.initializeAds(this, lifecycle.coroutineScope)
        openAppAdManager.initLoadAd( this, this, this)

    }

    override fun onLoadAdSuccess() {
        openAppAdManager.showAdIfAvailableAppOpen(this, this)
    }

    override fun onLoadAdFail() {
    }

    override fun onShowAdComplete() {
    }
}