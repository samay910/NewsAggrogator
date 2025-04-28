package com.samay910

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.samay910.module.initKoin
import com.samay910.networking.api_clients.news_api.NewsApiClient
import io.ktor.client.engine.darwin.Darwin
import networking.createHttpClient

fun MainViewController() = ComposeUIViewController(
    configure = {initKoin()}
) { App() }