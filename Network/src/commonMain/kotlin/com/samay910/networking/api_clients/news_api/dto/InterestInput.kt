package com.samay910.networking.api_clients.news_api.dto

import kotlinx.serialization.Serializable

@Serializable
data class InterestInput(
    val q: String,
    val category: String,
    val country: String,
    val from: String,
    val sortBy: String ,
    val pageSize: Int ,
    val page: Int,
    var to: String? = null
)
