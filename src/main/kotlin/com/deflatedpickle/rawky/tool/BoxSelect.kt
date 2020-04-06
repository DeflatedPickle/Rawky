/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.component.Toolbox
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import com.deflatedpickle.rawky.util.UsefulValues
import java.awt.Graphics2D
import java.awt.Polygon
import javax.swing.UIManager
import kotlin.math.max
import kotlin.math.min

class BoxSelect : HoverOutlineTool(null, "Box Select", listOf(Icons.boxSelect)) {
    var clickedCell: Pair<Int, Int>? = null

    var button = 1

    override fun mouseClicked(button: Int, polygon: Polygon, row: Int, column: Int, clickCount: Int) {
        PixelGrid.isSelectingFinished = false
        PixelGrid.selectedCells.clear()
        clickedCell = Pair(row, column)
    }

    override fun mouseRelease(button: Int, polygon: Polygon?, row: Int, column: Int) {
        super.mouseRelease(button, polygon, row, column)

        if (button != this.button) return

        PixelGrid.isSelectingFinished = true

        val pixel = object : Toolbox.LockCheck(this.name, false) {
            var selectedCells = mutableMapOf<PixelGrid.Cell, Boolean>()

            override fun perform() {
                clickedCell?.let {
                    selectedCells = process(column, row, it.second, it.first, PixelGrid.frameList[frame].layerList[layer].pixelMatrix)
                }
            }

            override fun cleanup() {
                for ((cell, selected) in selectedCells) {
                    cell.selected = selected
                }
            }

            override fun outline(g2D: Graphics2D) {
                for ((cell, _) in selectedCells) {
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

            process<Boolean>(column, row, it.second, it.first, PixelGrid.previewRectangleMatrix)
        }
    }

    override fun mouseMoved(polygon: Polygon, row: Int, column: Int) {
        with(PixelGrid.previewRectangleMatrix[row][column]) {
            colour = UsefulValues.currentColour
        }
    }

    override fun <T> process(x0: Int, y0: Int, x1: Int?, y1: Int?, cellMatrix: MutableList<MutableList<PixelGrid.Cell>>): MutableMap<PixelGrid.Cell, T> {
        val cellMap = mutableMapOf<PixelGrid.Cell, T>()

        y1?.let { y1NonNull ->
            x1?.let { x1NonNull ->
                val y0Temp = min(y0, y1NonNull)
                val y1Temp = max(y0, y1NonNull)

                for (row in y0Temp..y1Temp) {
                    val x0Temp = min(x0, x1NonNull)
                    val x1Temp = max(x0, x1NonNull)

                    for (column in x0Temp..x1Temp) {
                        with(cellMatrix[row][column]) {
                            selected = true
                            PixelGrid.selectedCells.add(this)
                        }
                        cellMap[cellMatrix[row][column]] = true as T
                    }
                }
            }
        }

        return cellMap
    }
}
