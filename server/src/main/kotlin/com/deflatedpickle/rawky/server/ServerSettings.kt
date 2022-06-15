package com.deflatedpickle.rawky.server

import com.deflatedpickle.rawky.server.ServerPlugin.portMax
import com.deflatedpickle.rawky.server.ServerPlugin.portMin
import com.deflatedpickle.rawky.server.backend.api.Encoder
import com.deflatedpickle.rawky.settings.api.IntRange
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class ServerSettings(
    @Required @IntRange(0, 75) var mouseSyncDelay: Int = 25,
    @Required var defaultHostName: String = "Host",
    @Required var defaultUserName: String = "User",
    @Required @IntRange(portMin, portMax) var defaultTCPPort: Int = 50_000,
    @Required @IntRange(portMin, portMax) var defaultUDPPort: Int = 50_000,
    @Required var defaultConnectionEncoding: String = "",
)