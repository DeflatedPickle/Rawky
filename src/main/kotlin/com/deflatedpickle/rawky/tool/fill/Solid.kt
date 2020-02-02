package com.deflatedpickle.rawky.tool.fill

import com.deflatedpickle.rawky.component.PixelGrid
import java.awt.Color

class Solid : Fill {
    override fun perform(cell: PixelGrid.Cell, row: Int, column: Int, shade: Color) {
        cell.colour = shade
    }
}