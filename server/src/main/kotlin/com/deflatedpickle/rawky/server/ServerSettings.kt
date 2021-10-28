package com.deflatedpickle.rawky.server

import kotlinx.serialization.Serializable

@Serializable
data class ServerSettings(
    val mouseSyncDelay: Int = 25,
    val defaultHostName: String = "Host",
)