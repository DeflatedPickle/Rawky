package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.api.Options
import com.deflatedpickle.rawky.api.Range
import com.deflatedpickle.rawky.api.Tooltip
import com.deflatedpickle.rawky.component.Toolbox
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Toolkit
import javax.swing.UIManager

class Eraser : HoverOutlineTool(Settings::class.java, "Eraser", Icons.eraser, Toolkit.getDefaultToolkit().createCustomCursor(Icons.eraser.image, Point(8, 8), "Eraser")) {
    @Options
    object Settings {
        @Range(1, 9)
        @Tooltip("Change the size of the eraser")
        @JvmField
        var size = 1
    }

    override fun performLeft(dragged: Boolean, point: Point, lastPoint: Point, clickCount: Int) {
        if (Components.pixelGrid
                        .frameList[Components.animationTimeline.list.selectedIndex]
                        .layerList[Components.layerList.list.selectedRow]
                        .pixelMatrix[Components.pixelGrid.hoverRow][Components.pixelGrid.hoverColumn]
                        .colour
                != null) {
            val pixel = object : Toolbox.LockCheck(this.cursor.name) {
                val oldValues = mutableListOf<MutableList<Color?>>()

                val size = Settings.size

                override fun perform() {
                    if (check()) {
                        for (sizeRow in 0 until size) {
                            val list = mutableListOf<Color?>()
                            for (sizeColumn in 0 until size) {
                                with(Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row + sizeRow][column + sizeColumn]) {
                                    list.add(this.colour)
                                    colour = null
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
                                with(Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row + sizeRow][column + sizeColumn]) {
                                    colour = oldValues[rowIndex][rowColumn]
                                }
                            }
                        }
                    }
                }

                override fun outline(g2D: Graphics2D) {
                    g2D.color = UIManager.getColor("List.selectionBackground")
                    g2D.drawRect(this.row * Components.pixelGrid.pixelSize, this.column * Components.pixelGrid.pixelSize, Components.pixelGrid.pixelSize, Components.pixelGrid.pixelSize)
                }
            }

            if (pixel.check()) {
                ActionStack.push(pixel)
            }
        }
    }
}