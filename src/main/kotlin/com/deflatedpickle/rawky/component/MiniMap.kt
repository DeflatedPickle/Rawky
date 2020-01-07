/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.annotations.RedrawSensitive
import com.deflatedpickle.rawky.api.component.Component
import com.deflatedpickle.rawky.util.Components
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Cursor
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.geom.Rectangle2D
import kotlin.math.max
import kotlin.math.min

@RedrawSensitive<PixelGrid>(PixelGrid::class)
class MiniMap : Component() {
    var drawAreaColour = Color.CYAN
    var handleColour = Color.RED
    var handleStroke = BasicStroke(2f)

    var scale = 0.2

    init {
        isOpaque = false

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                moveView(e)
            }
        })

        addMouseMotionListener(object : MouseMotionAdapter() {
            var inside = false

            override fun mouseMoved(e: MouseEvent) {
                with(PixelGrid.visibleRect) {
                    if (Rectangle2D.Double((x * PixelGrid.scale) * scale, (y * PixelGrid.scale) * scale, (width * PixelGrid.scale) * scale, (height * PixelGrid.scale) * scale).contains(e.point)) {
                        cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)
                        inside = true
                    } else {
                        cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
                        inside = false
                    }
                }
            }

            override fun mouseDragged(e: MouseEvent) {
                if (inside) {
                    // TODO: Properly map points from the mini-map to the pixel grid
                    moveView(e)
                }
            }
        })
    }

    fun moveView(e: MouseEvent) {
        PixelGrid.SCROLLABLE_INSTANCE.viewport.viewPosition = Point(
                max(0.0, min((e.x / scale) * PixelGrid.scale, (this@MiniMap.width / scale) * PixelGrid.scale) - PixelGrid.visibleRect.width * PixelGrid.scale).toInt(),
                max(0.0, min((e.y / scale) * PixelGrid.scale, (this@MiniMap.height / scale) * PixelGrid.scale) - PixelGrid.visibleRect.height * PixelGrid.scale).toInt()
        )
    }

    override fun paintComponent(g: Graphics) {
        val g2D = g as Graphics2D

        PixelGrid.drawTransparentBackground(g2D, fillType = PixelGrid.FillType.ALL, backgroundPixelDivider = 21)

        g2D.scale(scale, scale)

        for ((layerIndex, layer) in PixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList.withIndex().reversed()) {
            PixelGrid.drawPixels(layerIndex, layer, g2D)
        }

        g2D.color = this.drawAreaColour
        with(PixelGrid) {
            g2D.drawRect(0, 0, columnAmount * PixelGrid.Settings.pixelSize, rowAmount * PixelGrid.Settings.pixelSize)
        }

        g2D.color = this.handleColour
        g2D.stroke = this.handleStroke
        with(PixelGrid.visibleRect) {
            g2D.draw(Rectangle2D.Double(x / PixelGrid.scale, y / PixelGrid.scale, width / PixelGrid.scale, height / PixelGrid.scale))
        }
    }
}
