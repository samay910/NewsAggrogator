package com.samay910.database.local

import kotlinx.serialization.Serializable

@Serializable
data class LocalResponse(
    val id: Long,
    val q: String?,
    val topic: String?,
    val location: String?,
    val source: String?
)
