package com.samay910

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform