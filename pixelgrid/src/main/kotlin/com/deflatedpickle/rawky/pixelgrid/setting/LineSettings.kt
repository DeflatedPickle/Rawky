package com.deflatedpickle.rawky.pixelgrid.setting

import com.deflatedpickle.rawky.settings.api.FloatRange
import com.deflatedpickle.rawky.settings.widget.ConfigSection
import com.deflatedpickle.undulation.serializer.ColorSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import java.awt.Color

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class LineSettings(
    @Required var colour: @Serializable(ColorSerializer::class) Color = Color.BLACK,
    @Required @FloatRange(0.1f, 8.0f) var thickness: Float = 1f,
) : ConfigSection