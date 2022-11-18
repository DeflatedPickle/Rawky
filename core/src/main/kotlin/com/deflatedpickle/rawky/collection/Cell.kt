/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.collection

import com.deflatedpickle.undulation.serializer.ColorSerializer
import com.deflatedpickle.undulation.serializer.RectangleSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.awt.Color
import java.awt.Rectangle

@Serializable
data class Cell(
    val row: Int = 0,
    val column: Int = 0,
    @Transient val polygon: @Serializable(RectangleSerializer::class) Rectangle = defaultPolygon,
    var colour: @Serializable(ColorSerializer::class) Color = defaultColour,
) {
    lateinit var grid: Grid

    operator fun invoke(func: Cell.() -> Unit) = this.apply(func)

    companion object {
        val defaultPolygon = Rectangle()
        val defaultColour = Color(0, 0, 0, 0)
    }
}
