package com.deflatedpickle.rawky.util

import com.deflatedpickle.rawky.component.ColourHistory
import java.awt.Color

object UsefulValues {
    var currentColour = Color.BLACK
        set(value) {
            ColourHistory.addButton(field)
            field = value
        }
}