package com.deflatedpickle.rawky.server

import com.deflatedpickle.rawky.server.ServerPlugin.portMax
import com.deflatedpickle.rawky.server.ServerPlugin.portMin
import com.deflatedpickle.rawky.server.backend.util.Encoding
import com.deflatedpickle.rawky.settings.api.Range
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class ServerSettings(
    @Required @Range(0, 75) var mouseSyncDelay: Int = 25,
    @Required var defaultHostName: String = "Host",
    @Required var defaultUserName: String = "User",
    @Required @Range(portMin, portMax) var defaultTCPPort: Int = 50_000,
    @Required @Range(portMin, portMax) var defaultUDPPort: Int = 50_000,
    @Required var defaultConnectionEncoding: Encoding = Encoding.ASCII85,
)