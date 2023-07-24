/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tiledview

import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.rawky.settings.api.range.DoubleRange
import com.deflatedpickle.rawky.settings.api.range.IntRange
import kotlinx.serialization.Serializable

@Serializable
data class TiledViewSettings(
    override val version: Int = 1,
    @IntRange(0, 16) var rows: Int = 3,
    @IntRange(0, 16) var columns: Int = 3,
    @DoubleRange(0.0, 16.0) var xPadding: Double = 0.0,
    @DoubleRange(0.0, 16.0) var yPadding: Double = 0.0,
) : Config
