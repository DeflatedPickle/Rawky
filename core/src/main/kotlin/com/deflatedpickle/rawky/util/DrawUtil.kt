package com.deflatedpickle.rawky.util

import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.undulation.functions.extensions.toAwt
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point

object DrawUtil {
    fun paintGrid(g: Graphics, grid: Grid, outlineColour: Color = Color.BLACK) {
        for (cell in grid.children) {
            g.color = cell.colour.toAwt()
            g.fillRect(
                cell.polygon.x, cell.polygon.y,
                cell.polygon.width, cell.polygon.height
            )

            g.color = outlineColour
            g.drawRect(
                cell.polygon.x, cell.polygon.y,
                cell.polygon.width, cell.polygon.height
            )
        }
    }

    fun paintHoverCell(cells: List<Cell>, g: Graphics2D) {
        for (cell in cells) {
            g.stroke = BasicStroke(4f)
            g.color = Color.BLACK
            g.drawRect(
                cell.polygon.x, cell.polygon.y,
                cell.polygon.width, cell.polygon.height
            )
        }
    }
}