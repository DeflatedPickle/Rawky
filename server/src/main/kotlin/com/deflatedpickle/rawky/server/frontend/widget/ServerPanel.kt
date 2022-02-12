package com.deflatedpickle.rawky.server.frontend.widget

import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.event.EventUpdateCell
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.ServerPlugin.client
import com.deflatedpickle.rawky.server.ServerPlugin.server
import com.deflatedpickle.rawky.server.ServerSettings
import com.deflatedpickle.rawky.server.backend.event.EventStartServer
import com.deflatedpickle.rawky.server.backend.request.RequestMoveMouse
import com.deflatedpickle.rawky.server.backend.response.ResponseNewDocument
import com.deflatedpickle.rawky.server.backend.query.QueryUpdateCell
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.undulation.functions.extensions.toAwt
import kotlinx.datetime.Clock
import lc.kra.swing.BetterGlassPane
import org.apache.logging.log4j.LogManager
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.plaf.synth.SynthGraphicsUtils.paintIcon

object ServerPanel : BetterGlassPane() {
    private val logger = LogManager.getLogger()

    init {
        this.isOpaque = false

        var lastTime = Clock.System.now()

        this.addMouseMotionListener(object : MouseMotionAdapter() {
            fun mouse(drag: Boolean) {
                mousePosition?.let {
                    ConfigUtil.getSettings<ServerSettings>("deflatedpickle@server#*")?.let { s ->
                        if (Clock.System.now().toEpochMilliseconds() - lastTime.toEpochMilliseconds() > s.mouseSyncDelay) {
                            client.sendTCP(
                                RequestMoveMouse(
                                    ServerPanel.mousePosition,
                                    drag
                                )
                            )

                            lastTime = Clock.System.now()
                        }
                    }
                }
            }

            override fun mouseMoved(e: MouseEvent): Unit = mouse(false)
            override fun mouseDragged(e: MouseEvent): Unit = mouse(true)
        })

        EventStartServer.addListener {
            EventCreateDocument.addListener {
                sendGrid(it as RawkyDocument)
            }
        }

        EventUpdateCell.addListener {
            if (client.isConnected) {
                logger.debug("Sending cell update to server")
                client.sendTCP(QueryUpdateCell(it))
            }
            logger.debug("Sending cell update to all clients")
            server?.sendToAllExceptTCP(ServerPlugin.id, QueryUpdateCell(it))
        }
    }

    fun sendGrid(doc: RawkyDocument) {
        val frame = doc.children[doc.selectedIndex]
        val layer = frame.children[frame.selectedIndex]
        val grid = layer.child

        logger.debug("Sending grid data to all clients")

        server?.sendToAllTCP(
            ResponseNewDocument(
                grid.rows,
                grid.columns,
                doc.children.size,
                frame.children.size,
            )
        )
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

        drawUsers(g2D)
    }

    private fun drawUsers(g2D: Graphics2D) {
        // Draws every other clients cursor
        for ((index, user) in ServerPlugin.userMap) {
            // Dodge the current user
            if (index == ServerPlugin.id) continue

            g2D.color = user.colour.toAwt()

            val x = user.mousePosition.x
            val y = user.mousePosition.y

            g2D.drawString(
                user.userName,
                x + 8 - g2D.fontMetrics.stringWidth(user.userName) / 2,
                y - g2D.fontMetrics.height / 2
            )

            user.tool.icon.paintIcon(this, g2D, x, y)
        }
    }
}