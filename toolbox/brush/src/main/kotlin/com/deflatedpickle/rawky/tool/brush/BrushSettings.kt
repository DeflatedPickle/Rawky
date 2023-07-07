/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool.brush

import com.deflatedpickle.haruhi.api.config.Config
import kotlinx.serialization.Serializable

@Serializable
data class BrushSettings(
    override val version: Int = 1,
    var size: Int = 1,
) : Config
