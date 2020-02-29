/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.component.AnimationTimeline
import com.deflatedpickle.rawky.component.LayerList
import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.component.Toolbox
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import java.awt.Graphics2D
import java.awt.Polygon
import javax.swing.UIManager
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Move : HoverOutlineTool(null, "Move", listOf(Icons.move), Icons.move.image) {
    var clickedCell: Pair<Int, Int>? = null

    var button = 1

    override fun mouseClicked(button: Int, polygon: Polygon, row: Int, column: Int, clickCount: Int) {
        PixelGrid.isSelectingFinished = false
        clickedCell = Pair(row, column)
    }

    override fun mouseRelease(button: Int, polygon: Polygon?, row: Int, column: Int) {
        super.mouseRelease(button, polygon, row, column)

        if (button != this.button) return

        PixelGrid.isSelectingFinished = true

        val pixel = object : Toolbox.LockCheck(this.name, false) {
            var movedCells = mutableMapOf<PixelGrid.Cell, Boolean>()

            override fun perform() {
                clickedCell?.let {
                    movedCells = process(column, row, it.second, it.first, PixelGrid.frameList[frame].layerList[layer].pixelMatrix)
                }
            }

            override fun cleanup() {
                for ((cell, selected) in movedCells) {
                    cell.selected = selected
                }
            }

            override fun outline(g2D: Graphics2D) {
                for ((cell, _) in movedCells) {
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
                colour = Components.colourShades.selectedShade
            }

            process<Boolean>(column, row, it.second, it.first, PixelGrid.previewRectangleMatrix)
        }
    }

    override fun <T> process(x0: Int, y0: Int, x1: Int?, y1: Int?, cellMatrix: MutableList<MutableList<PixelGrid.Cell>>): MutableMap<PixelGrid.Cell, T> {
        val cellMap = mutableMapOf<PixelGrid.Cell, T>()

        val tempRemove = mutableListOf<PixelGrid.Cell>()
        val tempAdd = mutableListOf<PixelGrid.Cell>()

        PixelGrid.selectedCells.map { selectedCell ->
            selectedCell.selected = false
            tempRemove.add(selectedCell)

            y1?.let { y1NotNull ->
                x1?.let { x1NotNull ->
                    cellMatrix[y0][x0].colour = selectedCell.colour
                    cellMatrix[y0][x0].selected = true
                    tempAdd.add(cellMatrix[y0][x0])

                    cellMap[cellMatrix[y0][x0]] = cellMatrix[y0][x0].selected as T
                }
            }
        }

        tempRemove.map { PixelGrid.selectedCells.remove(it) }
        tempAdd.map { PixelGrid.selectedCells.add(it) }

        return cellMap
    }
}
