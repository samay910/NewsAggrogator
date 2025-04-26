package com.samay910.networking.api_clients.gemini_api.dto.body_json

import kotlinx.serialization.Serializable

@Serializable
data class GeminiInput(
    val contents: List<Content>
)