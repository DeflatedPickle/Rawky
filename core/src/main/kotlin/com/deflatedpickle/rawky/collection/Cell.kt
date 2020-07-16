package com.deflatedpickle.rawky.collection

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import java.awt.Color
import java.awt.Rectangle

@Serializable
data class Cell(
    val polygon: @ContextualSerialization Rectangle,
    val colour: @ContextualSerialization Color = defaultColour
) {
    companion object {
        val defaultColour = Color(0, 0, 0, 0)
    }
}