/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.setting

import com.deflatedpickle.haruhi.api.config.ConfigSection
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.pixelgrid.api.FillType
import com.deflatedpickle.rawky.settings.api.range.IntRange
import com.deflatedpickle.undulation.serializer.ColorSerializer
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class BackgroundSettings(
    var enabled: Boolean = true,
    var fill: FillType = FillType.GRID,
    @IntRange(1, Grid.pixel) var size: Int = Grid.pixel / 3,
    var even: @Serializable(ColorSerializer::class) Color = Color.LIGHT_GRAY,
    var odd: @Serializable(ColorSerializer::class) Color = Color.WHITE,
) : ConfigSection
