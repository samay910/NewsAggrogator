package com.samay910.networking.api_clients.news_api.dto

import kotlinx.serialization.Serializable

@Serializable
data class Article(
//    aspects of the api response can be null and thus require the null saftey parameters
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String,
    val urlToImage: String?
)