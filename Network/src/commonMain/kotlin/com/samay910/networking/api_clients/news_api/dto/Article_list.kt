package com.samay910.networking.api_clients.news_api.dto

data class Article_list(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)