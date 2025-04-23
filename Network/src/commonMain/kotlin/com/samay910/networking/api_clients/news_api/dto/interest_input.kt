package com.samay910.networking.api_clients.news_api.dto

import kotlinx.serialization.Serializable

@Serializable
data class interest_input(
    val q: String,
    val domain : String,
    val pageSize : Int,
    val pageNumber: Int
)
