/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool.fill

import com.deflatedpickle.rawky.api.annotations.Colour
import com.deflatedpickle.rawky.api.annotations.IntOpt
import com.deflatedpickle.rawky.api.annotations.Options
import com.deflatedpickle.rawky.api.annotations.Tooltip
import com.deflatedpickle.rawky.component.PixelGrid
import java.awt.Color

object Stipple : IFill {
    @Options
    object Settings {
        @Colour
        @Tooltip("The colour to use for the alternate colour")
        @JvmField
        var altColour: Color = PixelGrid.defaultColour()

        @IntOpt(1, 16)
        @Tooltip("The value to divide the row by, then use the remainder of")
        @JvmField
        var modulusRow = 2

        @IntOpt(1, 16)
        @Tooltip("The value to divide the column by, then use the remainder of")
        @JvmField
        var modulusColumn = 2
    }

    override fun perform(cell: PixelGrid.Cell, row: Int, column: Int, shade: Color) {
        if (row % Settings.modulusRow == column % Settings.modulusColumn) {
            cell.colour = shade
        } else {
            if (Settings.altColour != PixelGrid.defaultColour()) {
                cell.colour = Settings.altColour
            }
        }
    }
}
