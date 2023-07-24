/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool.brush

import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.rawky.settings.api.range.IntRange
import com.deflatedpickle.rawky.settings.api.widget.SliderSpinner
import kotlinx.serialization.Serializable

@Serializable
data class BrushSettings(
    override val version: Int = 1,
    @IntRange(1, 256) @SliderSpinner()  var size: Int = 1,
) : Config
