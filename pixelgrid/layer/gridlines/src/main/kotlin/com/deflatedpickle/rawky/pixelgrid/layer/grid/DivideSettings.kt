/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.layer.grid

import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.rawky.settings.api.range.FloatRange
import com.deflatedpickle.undulation.serializer.ColorSerializer
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class DivideSettings(
    override val version: Int = 1,
    var enabled: Boolean = true,
    var colour:
    @Serializable(ColorSerializer::class)
    Color = Color.BLACK,
    @FloatRange(0.1f, 8.0f) var thickness: Float = 1f,
) : Config
