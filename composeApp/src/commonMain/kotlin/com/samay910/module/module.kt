package com.samay910.module

import com.samay910.networking.api_clients.news_api.NewsApiClient
import com.samay910.screen.Home.HomeViewmodel
import networking.createHttpClient
import org.koin.core.context.startKoin
import org.koin.dsl.module

val networkModule= module {
//    create the atual HTTP client that will be used to call different API's
    single { createHttpClient(getHttpEngine()) }
    single { NewsApiClient(get()) }
    factory { HomeViewmodel(get()) }
}

fun initKoin() {
    startKoin{
        modules(networkModule)
    }
}