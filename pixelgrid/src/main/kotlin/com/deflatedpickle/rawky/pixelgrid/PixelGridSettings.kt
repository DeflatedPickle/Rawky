package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.rawky.settings.api.FloatRange
import com.deflatedpickle.rawky.settings.api.IntRange
import com.deflatedpickle.undulation.serializer.ColorSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import java.awt.Color

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class PixelGridSettings(
    @Required var lineColour: @Serializable(ColorSerializer::class) Color = Color.BLACK,
    @Required @FloatRange(0.1f, 8.0f) var lineThickness: Float = 1f,
)