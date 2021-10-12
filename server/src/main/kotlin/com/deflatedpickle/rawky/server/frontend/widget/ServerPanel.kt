package com.deflatedpickle.rawky.server.frontend.widget

import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.request.RequestMoveMouse
import lc.kra.swing.BetterGlassPane
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter

object ServerPanel : BetterGlassPane() {
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
        for ((index, user) in ServerPlugin.userMap) {
            // Dodge the current user
            if (index == ServerPlugin.id) continue

            val x = user.mousePosition.x
            val y = user.mousePosition.y

            g2D.drawString(
                user.userName,
                x + 8 - g.fontMetrics.stringWidth(user.userName) / 2,
                y - g.fontMetrics.height / 2
            )
            // This will later draw the tool selected by each user
            g2D.drawRect(x, y, 16, 16)
        }
    }
}