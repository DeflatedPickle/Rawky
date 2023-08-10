/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.grid.ascii.collection

import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.undulation.impl.NullGlyphVector
import com.deflatedpickle.undulation.serializer.ColorSerializer
import com.deflatedpickle.undulation.serializer.GlyphVectorSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.awt.Color
import java.awt.font.GlyphVector

@Serializable
@SerialName("ASCII")
data class ASCIICell(
    override val row: Int = 0,
    override val column: Int = 0,
    override var content: @Serializable(
        GlyphVectorSerializer::class
    ) GlyphVector = NullGlyphVector(),
    var colour: @Serializable(ColorSerializer::class) Color = Color.BLACK,
) : Cell<@Serializable(
    GlyphVectorSerializer::class
) GlyphVector>()
