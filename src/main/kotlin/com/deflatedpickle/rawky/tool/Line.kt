/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.component.Toolbox
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import com.deflatedpickle.rawky.util.UsefulValues
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Polygon
import javax.swing.UIManager
import kotlin.math.abs

class Line : HoverOutlineTool(null, "Line", listOf(Icons.line)) {
    // @Options
    // object Settings {
    //     @Enum("com.deflatedpickle.rawky.tool.Line\$Mode")
    //     @Tooltip("Changes the mode for the line")
    //     @JvmField
    //     var mode = Mode.SINGLE
    // }

    // enum class Mode {
    //     SINGLE,
    //     CONTINUOUS
    // }

    var clickedCell: Pair<Int, Int>? = null

    var button = 1

    override fun mouseClicked(button: Int, polygon: Polygon, row: Int, column: Int, clickCount: Int) {
        clickedCell = Pair(row, column)
    }

    override fun mouseRelease(button: Int, polygon: Polygon?, row: Int, column: Int) {
        super.mouseRelease(button, polygon, row, column)

        if (button != this.button) return

        val pixel = object : Toolbox.LockCheck(this.name, false) {
            var oldColours = mutableMapOf<PixelGrid.Cell, Color>()

            override fun perform() {
                clickedCell?.let {
                    oldColours = process(column, row, it.second, it.first, PixelGrid.frameList[frame].layerList[layer].pixelMatrix)
                }
            }

            override fun cleanup() {
                for ((cell, colour) in oldColours) {
                    cell.colour = colour
                }
            }

            override fun outline(g2D: Graphics2D) {
                for ((cell, _) in oldColours) {
                    g2D.color = UIManager.getColor("List.selectionBackground")
                    g2D.drawRect(
                            cell.row * PixelGrid.Settings.pixelSize,
                            cell.column * PixelGrid.Settings.pixelSize,
                            PixelGrid.Settings.pixelSize,
                            PixelGrid.Settings.pixelSize
                    )
                }
            }
        }

        if (pixel.check()) {
            ActionStack.push(pixel)
        }

        clickedCell = null
    }

    override fun mouseDragged(button: Int, polygon: Polygon?, row: Int, column: Int) {
        super.mouseDragged(button, polygon, row, column)

        clickedCell?.let {
            with(PixelGrid.previewRectangleMatrix[it.first][it.second]) {
                colour = UsefulValues.currentColour
            }

            process<Color>(column, row, it.second, it.first, PixelGrid.previewRectangleMatrix)
        }
    }

    override fun mouseMoved(polygon: Polygon, row: Int, column: Int) {
        with(PixelGrid.previewRectangleMatrix[row][column]) {
            colour = UsefulValues.currentColour
        }
    }

    override fun <T> process(x0: Int, y0: Int, x1: Int?, y1: Int?, cellMatrix: MutableList<MutableList<PixelGrid.Cell>>): MutableMap<PixelGrid.Cell, T> {
        // https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm#External_links
        var tempX0 = x0
        var tempY0 = y0

        val dx = abs(x1!! - tempX0)
        val sx = if (tempX0 < x1) 1 else -1
        val dy = -abs(y1!! - tempY0)
        val sy = if (tempY0 < y1) 1 else -1
        var err = dx + dy

        val cellMap = mutableMapOf<PixelGrid.Cell, Color>()

        while (true) {
            with(cellMatrix[tempY0][tempX0]) {
                cellMap[this] = colour
                colour = UsefulValues.currentColour
            }

            if (tempX0 == x1 && tempY0 == y1) break

            val e2 = 2 * err

            if (e2 >= dy) {
                err += dy
                tempX0 += sx
            }

            if (e2 <= dx) {
                err += dx
                tempY0 += sy
            }
        }

        return cellMap as MutableMap<PixelGrid.Cell, T>
    }
}
