package com.deflatedpickle.rawky.pixelcell.collection

import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.undulation.serializer.ColorSerializer
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class PixelCell(
    override val row: Int = 0,
    override val column: Int = 0,
    override var content: @Serializable(ColorSerializer::class) Color = defaultColour,
) : Cell<@Serializable(ColorSerializer::class) Color>()