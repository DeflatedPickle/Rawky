package com.deflatedpickle.rawky.collection

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.awt.Color
import java.awt.Rectangle

@Serializable
data class Cell(
    val polygon: @Contextual Rectangle,
    val colour: @Contextual Color = defaultColour
) {
    companion object {
        val defaultColour = Color(0, 0, 0, 0)
    }
}