package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.relation.SingleChild
import java.awt.Color
import java.awt.Polygon

data class Cell(
    override var parent: Layer,
    val row: Int,
    val column: Int,
    val polygon: Polygon,
    val colour: Color = defaultColour
) : SingleChild<Layer> {
    companion object {
        val defaultColour = Color(0, 0, 0, 0)
    }
}