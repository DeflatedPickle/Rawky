package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.util.Components
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.geom.Rectangle2D
import javax.swing.JPanel
import kotlin.math.max
import kotlin.math.min

class MiniMap : JPanel() {
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
                with(Components.pixelGrid.visibleRect) {
                    if (Rectangle2D.Double((x * Components.pixelGrid.scale) * scale, (y * Components.pixelGrid.scale) * scale, (width * Components.pixelGrid.scale) * scale, (height * Components.pixelGrid.scale) * scale).contains(e.point)) {
                        cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)
                        inside = true
                    }
                    else {
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
                max(0.0, min((e.x / scale) * Components.pixelGrid.scale, (this@MiniMap.width / scale) * Components.pixelGrid.scale) - Components.pixelGrid.visibleRect.width * Components.pixelGrid.scale).toInt(),
                max(0.0, min((e.y / scale) * Components.pixelGrid.scale, (this@MiniMap.height / scale) * Components.pixelGrid.scale) - Components.pixelGrid.visibleRect.height * Components.pixelGrid.scale).toInt()
        )
    }

    override fun paintComponent(g: Graphics) {
        val g2D = g as Graphics2D

        Components.pixelGrid.drawTransparentBackground(g2D, fillType = PixelGrid.FillType.ALL, backgroundPixelDivider = 21)

        g2D.scale(scale, scale)

        for ((layerIndex, layer) in Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList.withIndex().reversed()) {
            Components.pixelGrid.drawPixels(layerIndex, layer, g2D)
        }

        g2D.color = this.drawAreaColour
        with(Components.pixelGrid) {
            g2D.drawRect(0, 0, columnAmount * PixelGrid.Settings.pixelSize, rowAmount * PixelGrid.Settings.pixelSize)
        }

        g2D.color = this.handleColour
        g2D.stroke = this.handleStroke
        with(Components.pixelGrid.visibleRect) {
            g2D.draw(Rectangle2D.Double(x / Components.pixelGrid.scale, y / Components.pixelGrid.scale, width / Components.pixelGrid.scale, height / Components.pixelGrid.scale))
        }
    }
}