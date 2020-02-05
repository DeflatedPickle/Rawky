/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.api.annotations.Category
import com.deflatedpickle.rawky.api.annotations.Enum
import com.deflatedpickle.rawky.api.annotations.IntRangeOpt
import com.deflatedpickle.rawky.api.annotations.Options
import com.deflatedpickle.rawky.api.annotations.Toggle
import com.deflatedpickle.rawky.api.annotations.Tooltip
import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.component.Toolbox
import com.deflatedpickle.rawky.tool.fill.Fill
import com.deflatedpickle.rawky.tool.fill.Solid
import com.deflatedpickle.rawky.tool.fill.Stipple
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.util.ArrayDeque
import javax.swing.JPanel
import javax.swing.UIManager

class Bucket : HoverOutlineTool(Settings::class.java, "Bucket", listOf(Icons.bucket), Icons.bucket.image, false) {
    @Options
    object Settings {
        @Enum("com.deflatedpickle.rawky.tool.Bucket\$FillType")
        @Toggle("addFillOptions")
        @Tooltip("Changes the fill the bucket uses")
        @JvmField
        var fill = FillType.SOLID

        fun addFillOptions(option: Int, jPanel: JPanel) {
            jPanel.removeAll()

            for (clazz in FillType.values()[option].instance::class.java.declaredClasses) {
                if (clazz.annotations.map { it.annotationClass == Options::class }.contains(true)) {
                    for (field in clazz.fields) {
                        if (field.name != "INSTANCE") {
                            Components.processAnnotations(jPanel, field)
                        }
                    }
                }
            }

            Components.toolOptions.invalidate()
            Components.toolOptions.revalidate()
            Components.toolOptions.repaint()

            jPanel.invalidate()
            jPanel.revalidate()
            jPanel.repaint()
        }

        @Category
        object RGB {
            @IntRangeOpt(0, 255)
            @Tooltip("Change the range of red to fill")
            @JvmField
            var red = 0..255

            @IntRangeOpt(0, 255)
            @Tooltip("Change the range of green to fill")
            @JvmField
            var green = 0..255

            @IntRangeOpt(0, 255)
            @Tooltip("Change the range of blue to fill")
            @JvmField
            var blue = 0..255
        }

        @Category
        object HSB {
            @IntRangeOpt(0, 360)
            @Tooltip("Change the range of hue to fill")
            @JvmField
            var hue = 0..360

            @IntRangeOpt(0, 360)
            @Tooltip("Change the range of saturation to fill")
            @JvmField
            var saturation = 0..360

            @IntRangeOpt(0, 360)
            @Tooltip("Change the range of brightness to fill")
            @JvmField
            var brightness = 0..360
        }

        @IntRangeOpt(0, 255)
        @Tooltip("Change the range of opacity to fill")
        @JvmField
        var alpha = 0..255
    }

    enum class FillType(val instance: Fill) {
        SOLID(Solid),
        STIPPLE(Stipple)
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

                            val colour = cell.colour
                            val rgb = cell.colour.rgb
                            val hsb = Color.RGBtoHSB(colour.red, colour.green, colour.blue, null)

                            if (rgb == clickedColour.rgb &&
                                    colour.red in Settings.RGB.red &&
                                    colour.green in Settings.RGB.green &&
                                    colour.blue in Settings.RGB.blue &&
                                    hsb[0] in (Settings.HSB.hue.first.toFloat() / 360)..(Settings.HSB.hue.last.toFloat() / 360) &&
                                    hsb[1] in (Settings.HSB.saturation.first.toFloat() / 360)..(Settings.HSB.saturation.last.toFloat() / 360) &&
                                    hsb[2] in (Settings.HSB.brightness.first.toFloat() / 360)..(Settings.HSB.brightness.last.toFloat() / 360) &&
                                    colour.alpha in Settings.alpha) {
                                oldColours[cell] = cell.colour

                                Settings.fill.instance.perform(cell, this.first, this.second, shade)

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
