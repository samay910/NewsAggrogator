package com.samay910.module

import com.samay910.networking.api_clients.gemini_api.GoogleGeminiApiClient
import com.samay910.networking.api_clients.news_api.NewsApiClient
import com.samay910.screen.Headlines.HeadlinesViewmodel
import com.samay910.screen.Home.HomeViewmodel
import com.samay910.screen.Interests.add.AddInterestViewmodel
import com.samay910.screen.Interests.create_feed_form.CreateFeedFormViewmodel
import com.samay910.screen.Interests.viewFeed.InterestFeedViewmodel
import networking.createHttpClient
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

//this file ensures dependency injection using koin is possible on both platforms

//this is fine for the API calls and network activity but not for the local database.To deal with this i initialise koin on both respective platforms and then add necessary additional modules
val networkModule= module {
//    create the actual HTTP client that will be used to call different API's
    single { createHttpClient(getHttpEngine()) }
    single { NewsApiClient(get()) }
    single { GoogleGeminiApiClient(get()) }
    factory { HomeViewmodel(get(),get())}
    factory { HeadlinesViewmodel(get()) }
    factory { AddInterestViewmodel(get()) }
    factory { CreateFeedFormViewmodel(get()) }
    factory { InterestFeedViewmodel(get()) }
}
//this is the module holding the native platform specific dependencies required for local storage
expect val platformModule: Module

//the parameter is just required to configure the android side of things
fun initKoin(config:(KoinApplication.()->Unit)?=null) {
    startKoin{
        config?.invoke(this)
        modules(
            platformModule,
            networkModule)
    }
}

