@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourhistory

import com.deflatedpickle.haruhi.api.Config
import com.deflatedpickle.undulation.serializer.ColorSerializer
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class ColourHistorySettings (
    override val version: Int = 1,
    val history: MutableList<@Serializable(ColorSerializer::class) Color> = mutableListOf(),
    var historyLength: Int = 24,
) : Config