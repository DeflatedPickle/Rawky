/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.setting

import com.deflatedpickle.rawky.settings.widget.ConfigSection
import com.deflatedpickle.undulation.serializer.ColorSerializer
import com.deflatedpickle.undulation.serializer.FontSerializer
import kotlinx.serialization.Serializable
import java.awt.Color
import java.awt.Font

@Serializable
data class DebugSettings(
    var enabled: Boolean = false,
    var colour: @Serializable(ColorSerializer::class) Color = Color.PINK,
    var font: @Serializable(FontSerializer::class) Font = Font(Font.DIALOG, Font.PLAIN, 20)
) : ConfigSection
