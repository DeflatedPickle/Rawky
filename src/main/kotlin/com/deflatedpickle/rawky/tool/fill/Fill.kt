/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool.fill

import com.deflatedpickle.rawky.component.PixelGrid
import java.awt.Color

interface Fill {
    fun perform(cell: PixelGrid.Cell, row: Int, column: Int, shade: Color)
}
