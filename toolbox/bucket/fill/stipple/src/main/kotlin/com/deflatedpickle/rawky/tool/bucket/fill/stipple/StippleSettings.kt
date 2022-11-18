/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool.bucket.fill.stipple

import com.deflatedpickle.haruhi.api.Config
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.settings.api.range.IntRange
import com.deflatedpickle.undulation.serializer.ColorSerializer
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class StippleSettings(
    override val version: Int = 1,
    var altColour: @Serializable(ColorSerializer::class) Color = RawkyPlugin.colour,
    @IntRange(1, 16) var modulusRow: Int = 2,
    @IntRange(1, 16) var modulusColumn: Int = 2,
) : Config
