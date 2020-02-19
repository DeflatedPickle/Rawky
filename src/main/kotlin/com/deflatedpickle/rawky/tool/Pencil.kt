/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.api.annotations.IntOpt
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
import java.awt.Polygon
import javax.swing.UIManager

class Pencil : HoverOutlineTool(Settings::class.java, "Pencil", listOf(Icons.pencil), Icons.pencil.image,
        Toolbox.Group.PRIMARY) {
    @Options
    object Settings {
        @IntOpt(1, 9)
        @Tooltip("Change the size of the pencil")
        @JvmField
        var size = 1
    }

    override fun perform(button: Int, dragged: Boolean, point: Point, lastPoint: Point?, clickCount: Int) {
        if (PixelGrid
                        .frameList[Components.animationTimeline.list.selectedIndex]
                        .layerList[Components.layerList.table.selectedRow]
                        .pixelMatrix[PixelGrid.hoverRow][PixelGrid.hoverColumn]
                        .colour
                != Components.colourShades.selectedShade) {
            val pixel = object : Toolbox.LockCheck(this.name) {
                // There'll only be one new value
                val newValue = Components.colourShades.selectedShade
                // But there can be multiple old values
                val oldValues = mutableListOf<MutableList<Color>>()

                val size = Settings.size

                override fun perform() {
                    if (check()) {
                        for (sizeRow in 0 until size) {
                            val list = mutableListOf<Color>()
                            for (sizeColumn in 0 until size) {
                                with(PixelGrid.frameList[frame].layerList[layer].pixelMatrix[row + sizeRow][column + sizeColumn]) {
                                    list.add(this.colour)
                                    colour = newValue
                                }
                            }
                            oldValues.add(list)
                        }
                    }
                }

                override fun cleanup() {
                    if (check()) {
                        for ((rowIndex, sizeRow) in (0 until size).withIndex()) {
                            for ((rowColumn, sizeColumn) in (0 until size).withIndex()) {
                                with(PixelGrid.frameList[frame].layerList[layer].pixelMatrix[row + sizeRow][column + sizeColumn]) {
                                    colour = oldValues[rowIndex][rowColumn]
                                }
                            }
                        }
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

    override fun mouseMoved(polygon: Polygon, row: Int, column: Int) {
        with(PixelGrid.previewRectangleMatrix[row][column]) {
            colour = Components.colourShades.selectedShade
        }
    }
}
