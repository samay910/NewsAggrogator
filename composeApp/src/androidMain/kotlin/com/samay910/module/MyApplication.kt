package com.samay910.module

import android.app.Application
import org.koin.android.ext.koin.androidContext


//this is added t the manifest to ensure on the application boot this is ran alongside the main activity
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
//        this configuration is required only by android
        initKoin(
            config = { androidContext(this@MyApplication) }
        )
    }
}