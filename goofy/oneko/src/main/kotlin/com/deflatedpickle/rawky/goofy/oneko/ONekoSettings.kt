package com.deflatedpickle.rawky.goofy.oneko

import com.deflatedpickle.haruhi.api.config.Config
import kotlinx.serialization.Serializable

@Serializable
data class ONekoSettings(
    override val version: Int= 1,
    var pack: ONekoPack = ONekoPack.NEKO,
    var setCursor: Boolean = false,
) : Config
