/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.layer.onionskin

import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.rawky.settings.api.range.IntRange
import com.deflatedpickle.undulation.serializer.ColorSerializer
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class OnionSkinSettings(
    override val version: Int = 1,
    var enabled: Boolean = true,
    @IntRange(1, 15) var previousFrames: Int = 4,
    @IntRange(1, 15) var futureFrames: Int = 4,
    var skinStratergy: SkinStratergy = SkinStratergy.MONOTONE,
    var previousColour:
    @Serializable(ColorSerializer::class)
    Color = Color.YELLOW,
    var futureColour:
    @Serializable(ColorSerializer::class)
    Color = Color.MAGENTA,
) : Config
