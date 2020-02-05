/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool.fill

import com.deflatedpickle.rawky.api.annotations.Colour
import com.deflatedpickle.rawky.api.annotations.Options
import com.deflatedpickle.rawky.api.annotations.Tooltip
import com.deflatedpickle.rawky.component.PixelGrid
import java.awt.Color

object Stipple : Fill {
    @Options
    object Settings {
        @Colour
        @Tooltip("The colour to use for the alternate colour")
        @JvmField
        var altColour: Color = PixelGrid.defaultColour()
    }

    override fun perform(cell: PixelGrid.Cell, row: Int, column: Int, shade: Color) {
        if (row % 2 == column % 2) {
            cell.colour = shade
        } else {
            if (Settings.altColour != PixelGrid.defaultColour()) {
                cell.colour = Settings.altColour
            }
        }
    }
}
