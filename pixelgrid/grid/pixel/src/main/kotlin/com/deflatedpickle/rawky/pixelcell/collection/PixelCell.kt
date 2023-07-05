/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelcell.collection

import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.undulation.serializer.ColorSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
@SerialName("Pixel")
data class PixelCell(
    override val row: Int = 0,
    override val column: Int = 0,
    override var content: @Serializable(ColorSerializer::class) Color = default,
) : Cell<@Serializable(ColorSerializer::class) Color>() {
    companion object {
        val default = Color(0, 0, 0, 0)
    }
}
