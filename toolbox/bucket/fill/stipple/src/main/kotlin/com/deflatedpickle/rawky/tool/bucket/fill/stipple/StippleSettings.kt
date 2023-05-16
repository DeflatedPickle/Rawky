/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool.bucket.fill.stipple

import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.rawky.settings.api.range.IntRange
import kotlinx.serialization.Serializable

@Serializable
data class StippleSettings(
    override val version: Int = 1,
    @IntRange(1, 16) var modulusRow: Int = 2,
    @IntRange(1, 16) var modulusColumn: Int = 2,
) : Config
