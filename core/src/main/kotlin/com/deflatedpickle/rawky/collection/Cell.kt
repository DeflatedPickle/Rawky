package com.deflatedpickle.rawky.collection

import com.deflatedpickle.marvin.Colour
import com.deflatedpickle.undulation.serializer.ColorSerializer
import com.deflatedpickle.undulation.serializer.RectangleSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import java.awt.Color
import java.awt.Rectangle

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Cell(
    val row: Int = 0,
    val column: Int = 0,
    val polygon: @Serializable(RectangleSerializer::class) Rectangle = defaultPolygon,
    @Required var colour: @Serializable(ColorSerializer::class) Color = defaultColour,
) {
    operator fun invoke(func: Cell.() -> Unit) = this.apply(func)

    companion object {
        val defaultPolygon = Rectangle()
        val defaultColour = Color(0, 0, 0, 0)
    }
}