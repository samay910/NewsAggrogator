package com.samay910.networking.api_clients.news_api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArticleList(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)