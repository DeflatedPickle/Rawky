/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool.fill

import com.deflatedpickle.rawky.component.PixelGrid
import java.awt.Color

object Solid : IFill {
    override fun perform(cell: PixelGrid.Cell, row: Int, column: Int, shade: Color) {
        cell.colour = shade
    }
}
