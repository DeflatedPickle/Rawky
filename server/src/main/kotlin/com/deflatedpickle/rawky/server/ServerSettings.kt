/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server

import com.deflatedpickle.haruhi.api.Config
import com.deflatedpickle.rawky.server.ServerPlugin.portMax
import com.deflatedpickle.rawky.server.ServerPlugin.portMin
import com.deflatedpickle.rawky.settings.api.range.IntRange
import com.deflatedpickle.rawky.settings.api.widget.SliderSpinner
import kotlinx.serialization.Serializable

@Serializable
data class ServerSettings(
    override val version: Int = 1,
    @IntRange(0, 75) var mouseSyncDelay: Int = 25,
    var defaultHostName: String = "Host",
    var defaultUserName: String = "User",
    @IntRange(portMin, portMax) @SliderSpinner var defaultTCPPort: Int = 50_000,
    @IntRange(portMin, portMax) @SliderSpinner var defaultUDPPort: Int = 50_000,
    var defaultConnectionEncoding: String = "",
) : Config
