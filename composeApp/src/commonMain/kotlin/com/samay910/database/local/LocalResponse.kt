package com.samay910.database.local

import kotlinx.serialization.Serializable

@Serializable
data class LocalResponse(
    val id: Long,
    var q: String = "unset",
    var topic: String = "unset",
    var location: String = "unset",
    var source: String="unset"
)
