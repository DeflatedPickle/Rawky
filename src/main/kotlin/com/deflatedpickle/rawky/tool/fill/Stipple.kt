package com.deflatedpickle.rawky.tool.fill

import com.deflatedpickle.rawky.component.PixelGrid
import java.awt.Color

class Stipple : Fill {
    override fun perform(cell: PixelGrid.Cell, row: Int, column: Int, shade: Color) {
        if (row % 2 == column % 2) {
            cell.colour = shade
        }
    }
}