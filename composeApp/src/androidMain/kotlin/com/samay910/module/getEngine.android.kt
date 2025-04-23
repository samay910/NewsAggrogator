package com.samay910.module

import androidx.compose.runtime.remember
import com.samay910.networking.api_clients.news_api.NewsApiClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import networking.createHttpClient

actual fun getHttpEngine(): HttpClientEngine {
    return OkHttp.create()
}