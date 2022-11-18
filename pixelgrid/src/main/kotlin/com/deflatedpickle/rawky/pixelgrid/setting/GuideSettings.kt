package com.deflatedpickle.rawky.pixelgrid.setting

import com.deflatedpickle.rawky.settings.api.range.FloatRange
import com.deflatedpickle.rawky.settings.widget.ConfigSection
import com.deflatedpickle.undulation.serializer.ColorSerializer
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class GuideSettings(
    var colour: @Serializable(ColorSerializer::class) Color = Color.PINK,
    @FloatRange(0.1f, 8.0f) var thickness: Float = 0.5f,
) : ConfigSection