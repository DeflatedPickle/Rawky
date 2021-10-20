package com.deflatedpickle.rawky.collection

import com.deflatedpickle.marvin.Colour
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.awt.Rectangle

@Serializable
data class Cell(
    val row: Int = 0,
    val column: Int = 0,
    val polygon: @Contextual Rectangle = defaultPolygon,
    var colour: @Contextual Colour = defaultColour,
) {
    operator fun invoke(func: Cell.() -> Unit) = this.apply(func)

    companion object {
        val defaultPolygon = Rectangle()
        val defaultColour = Colour(0, 0, 0, 0)
    }
}