package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.EComponent
import java.awt.*
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

        addMouseMotionListener(object : MouseMotionAdapter() {
            var inside = false

            override fun mouseMoved(e: MouseEvent) {
                with(Components.pixelGrid.visibleRect) {
                    if (Rectangle2D.Double(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble()).apply { setFrame(x * scale, y * scale, width * scale, height * scale) }.contains(e.point)) {
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
                    PixelGrid.SCROLLABLE_INSTANCE.viewport.viewPosition = Point(
                            max(0.0, min(e.x / scale, this@MiniMap.width / scale) - Components.pixelGrid.visibleRect.width).toInt(),
                            max(0.0, min(e.y / scale, this@MiniMap.height / scale) - Components.pixelGrid.visibleRect.height).toInt()
                    )
                }
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        val g2D = g as Graphics2D

        Components.pixelGrid.drawTransparentBackground(g2D, fillType = PixelGrid.FillType.ALL, backgroundPixelDivider = 21)

        g2D.scale(scale, scale)

        for ((layerIndex, layer) in Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList.withIndex().reversed()) {
            Components.pixelGrid.drawPixels(layerIndex, layer, g2D, EComponent.MINI_MAP)
        }

        g2D.color = this.drawAreaColour
        with(Components.pixelGrid) {
            g2D.drawRect(0, 0, columnAmount * pixelSize, rowAmount * pixelSize)
        }

        g2D.color = this.handleColour
        g2D.stroke = this.handleStroke
        g2D.draw(Components.pixelGrid.visibleRect)
    }
}