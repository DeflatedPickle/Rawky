/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.layer.guides

import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.rawky.settings.api.range.FloatRange
import com.deflatedpickle.undulation.serializer.ColorSerializer
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class GuideSettings(
    override val version: Int = 1,
    var colour: @Serializable(ColorSerializer::class) Color = Color.PINK,
    @FloatRange(0.1f, 8.0f) var thickness: Float = 0.5f,
) : Config
