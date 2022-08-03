package com.deflatedpickle.rawky.tool.bucket.fill.stipple

import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.settings.api.IntRange
import com.deflatedpickle.undulation.serializer.ColorSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class StippleSettings(
    @Required var altColour: @Serializable(ColorSerializer::class) Color = RawkyPlugin.colour,
    @Required @IntRange(1, 16) var modulusRow: Int = 2,
    @Required @IntRange(1, 16) var modulusColumn: Int = 2
)