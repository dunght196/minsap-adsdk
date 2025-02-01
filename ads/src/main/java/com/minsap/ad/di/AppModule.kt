package com.minsap.ad.di

import android.content.Context
import com.minsap.ad.ConfigAdManager
import com.minsap.ad.GoogleMobileAdsConsentManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideGoogleMobileAdsConsentManager(
        @ApplicationContext context: Context
    ): GoogleMobileAdsConsentManager =
        GoogleMobileAdsConsentManager(context)

    @Singleton
    @Provides
    fun provideConfigAdManager(): ConfigAdManager = ConfigAdManager()

    @Provides
    fun providesGGCoroutineDispatchers(): GGCoroutineDispatchers {
        return GGCoroutineDispatchers(
            io = Dispatchers.IO,
            computation = Dispatchers.Default,
            main = Dispatchers.Main,
        )
    }
}