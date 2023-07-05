/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tilecell.collection

import com.deflatedpickle.marvin.serializer.BufferedImageSerializer
import com.deflatedpickle.rawky.collection.Cell
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.awt.image.BufferedImage

@Serializable
@SerialName("Tile")
data class TileCell(
    override val row: Int = 0,
    override val column: Int = 0,
    override var content: @Serializable(BufferedImageSerializer::class) BufferedImage = default,
) : Cell<@Serializable(BufferedImageSerializer::class) BufferedImage>() {
    companion object {
        val default = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    }
}
