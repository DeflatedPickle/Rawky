package com.deflatedpickle.rawky.server.frontend.widget

import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.request.RequestMoveMouse
import com.deflatedpickle.rawky.ui.window.Window
import lc.kra.swing.BetterGlassPane
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.RenderingHints
import java.awt.Toolkit
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter

object ServerPanel : BetterGlassPane() {
    val cursorPositions = hashMapOf<Int, Point>()

    init {
        this.isOpaque = false

        this.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                ServerPlugin.client.sendTCP(
                    RequestMoveMouse(
                        ServerPanel.mousePosition
                    )
                )
            }

            override fun mouseDragged(e: MouseEvent) {
                ServerPlugin.client.sendTCP(
                    RequestMoveMouse(
                        ServerPanel.mousePosition
                    )
                )
            }
        })
    }

    override fun repaint() {
        super.invalidate()
        super.revalidate()

        super.repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val g2D = g as Graphics2D
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        g2D.color = Color.BLACK

        // Draws every other clients cursor
        for ((index, position) in this.cursorPositions) {
            // Dodge the current user
            if (index == ServerPlugin.id) return

            val x = position.x
            val y = position.y

            g2D.drawString(
                "$index",
                x + 8 - g.fontMetrics.stringWidth("$index") / 2,
                y - g.fontMetrics.height / 2
            )
            g2D.fillRect(x, y, 16, 16)
        }
    }
}