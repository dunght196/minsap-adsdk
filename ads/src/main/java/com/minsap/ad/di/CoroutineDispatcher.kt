package com.minsap.ad.di

import kotlinx.coroutines.CoroutineDispatcher

data class GGCoroutineDispatchers(
    val io: CoroutineDispatcher,
    val computation: CoroutineDispatcher,
    val main: CoroutineDispatcher,
)