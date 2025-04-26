package com.samay910.networking.api_clients.gemini_api.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PartResponse(
    val text: String
)