/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.api.annotations.Category
import com.deflatedpickle.rawky.api.annotations.IntRangeOpt
import com.deflatedpickle.rawky.api.annotations.Options
import com.deflatedpickle.rawky.api.annotations.Tooltip
import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.component.Toolbox
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.util.ArrayDeque
import javax.swing.UIManager

class Bucket : HoverOutlineTool(Settings::class.java, "Bucket", listOf(Icons.bucket), Icons.bucket.image, false) {
    @Options
    object Settings {
        @Category
        object Ranges {
            @IntRangeOpt(0, 255)
            @Tooltip("Change the range of opacity to fill")
            @JvmField
            var alphaRange = 0..255
        }
    }

    override fun perform(button: Int, dragged: Boolean, point: Point, lastPoint: Point?, clickCount: Int) {
        val pixel = object : Toolbox.LockCheck(this.name) {
            val shade = Components.colourShades.selectedShade
            val clickedColour = PixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour

            // The cell and the colour it used to be
            val oldColours = mutableMapOf<PixelGrid.Cell, Color>()

            override fun perform() {
                val cellList = ArrayDeque<Pair<Int, Int>>()

                cellList.add(Pair(row, column))

                // http://steve.hollasch.net/cgindex/polygons/floodfill.html
                while (cellList.isNotEmpty()) {
                    with(cellList.poll()) {
                        if (this.first in 0 until PixelGrid.rowAmount &&
                                this.second in 0 until PixelGrid.columnAmount) {
                            val cell = PixelGrid.frameList[frame].layerList[layer].pixelMatrix[this.first][this.second]

                            if (cell.colour.rgb == clickedColour.rgb && cell.colour.alpha in Settings.Ranges.alphaRange) {
                                oldColours[cell] = cell.colour
                                cell.colour = shade

                                cellList.add(Pair(this.first, this.second + 1))
                                cellList.add(Pair(this.first, this.second - 1))
                                cellList.add(Pair(this.first + 1, this.second))
                                cellList.add(Pair(this.first - 1, this.second))
                            }
                        }
                    }
                }
            }

            override fun cleanup() {
                for ((k, v) in oldColours) {
                    k.colour = v
                }
            }

            override fun outline(g2D: Graphics2D) {
                g2D.color = UIManager.getColor("List.selectionBackground")
                g2D.drawRect(this.row * PixelGrid.Settings.pixelSize, this.column * PixelGrid.Settings.pixelSize, PixelGrid.Settings.pixelSize, PixelGrid.Settings.pixelSize)
            }
        }

        if (pixel.check()) {
            ActionStack.push(pixel)
        }
    }
}
