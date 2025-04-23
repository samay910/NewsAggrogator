package com.samay910.module

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual fun getHttpEngine(): HttpClientEngine {
    return Darwin.create()
}