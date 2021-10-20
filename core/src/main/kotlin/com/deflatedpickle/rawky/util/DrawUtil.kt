package com.deflatedpickle.rawky.util

import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.undulation.extensions.toAwt
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point

object DrawUtil {
    fun paintGrid(g: Graphics, grid: Grid) {
        for (cell in grid.children) {
            g.color = cell.colour.toAwt()
            g.fillRect(
                cell.polygon.x, cell.polygon.y,
                cell.polygon.width, cell.polygon.height
            )

            g.color = Color.BLACK
            g.drawRect(
                cell.polygon.x, cell.polygon.y,
                cell.polygon.width, cell.polygon.height
            )
        }
    }

    fun paintHoverCell(mousePosition: Point, g: Graphics2D, grid: Grid) {
        for (cell in grid.children) {
            if (cell.polygon.contains(mousePosition)) {
                g.stroke = BasicStroke(4f)
                g.color = Color.BLACK
                g.drawRect(
                    cell.polygon.x, cell.polygon.y,
                    cell.polygon.width, cell.polygon.height
                )
            }
        }
    }
}