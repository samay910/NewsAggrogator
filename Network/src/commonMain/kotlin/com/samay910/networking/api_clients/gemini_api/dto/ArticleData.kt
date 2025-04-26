package com.samay910.networking.api_clients.gemini_api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArticleData(
    val articleDescriptions: MutableList<String>,
)
