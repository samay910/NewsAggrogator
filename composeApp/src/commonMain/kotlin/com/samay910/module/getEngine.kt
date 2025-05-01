package com.samay910.module

import io.ktor.client.engine.HttpClientEngine

//this gets the native http client engine for both ios and android
expect fun getHttpEngine():HttpClientEngine