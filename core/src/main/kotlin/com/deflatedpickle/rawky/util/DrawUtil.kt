/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.util

import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.collection.Grid
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Stroke

object DrawUtil {
    fun paintGrid(g: Graphics2D, grid: Grid, outlineColour: Color = Color.BLACK, stroke: Stroke = BasicStroke(1f)) {
        paintGridFill(g, grid, outlineColour)
        paintGridOutline(g, grid, outlineColour, stroke)
    }

    fun paintGridOutline(g: Graphics2D, grid: Grid, outlineColour: Color = Color.BLACK, stroke: Stroke) {
        for (cell in grid.children) {
            g.stroke = stroke
            g.color = outlineColour
            g.drawRect(
                cell.polygon.x, cell.polygon.y,
                cell.polygon.width, cell.polygon.height
            )
        }
    }

    fun paintGridFill(g: Graphics2D, grid: Grid, outlineColour: Color = Color.BLACK) {
        for (cell in grid.children) {
            CellProvider.current.paintGrid(
                g, cell
            )
        }
    }

    fun paintHoverCell(cells: MutableList<Cell<out Any>>, g: Graphics2D) {
        for (cell in cells) {
            CellProvider.current.paintHover(
                g, cell
            )
        }
    }
}
