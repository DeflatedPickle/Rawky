/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourshades

import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.rawky.settings.api.range.IntRange
import kotlinx.serialization.Serializable

@Serializable
data class ColourShadesSettings(
    override val version: Int = 1,
    @IntRange(1, 25) var lightShades: Int = 4,
    @IntRange(1, 25) var darkShades: Int = 4,
    @IntRange(1, 255) var brightnessStep: Int = 25,
) : Config
