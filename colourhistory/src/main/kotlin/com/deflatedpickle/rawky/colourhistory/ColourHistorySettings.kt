/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourhistory

import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.rawky.settings.api.range.IntRange
import com.deflatedpickle.undulation.serializer.ColorSerializer
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class ColourHistorySettings(
    override val version: Int = 1,
    val history: MutableList<
        @Serializable(ColorSerializer::class)
        Color,
        > = mutableListOf(),
    @IntRange(1, 100) var historyLength: Int = 24,
) : Config
