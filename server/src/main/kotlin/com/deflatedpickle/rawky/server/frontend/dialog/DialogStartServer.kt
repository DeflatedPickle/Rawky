package com.deflatedpickle.rawky.server.frontend.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.ui.constraints.StickEast
import com.deflatedpickle.rawky.ui.constraints.StickEastFinishLine
import com.deflatedpickle.rawky.ui.constraints.StickWestFinishLine
import com.deflatedpickle.tosuto.constraints.FillHorizontalFinishLine
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Dimension
import java.awt.GridBagLayout
import java.io.IOException
import javax.swing.JCheckBox
import javax.swing.JPanel

class DialogStartServer : TaskDialog(PluginUtil.window, "Start a Server") {
    companion object {
        fun open() {
            val dialog = DialogStartServer()

            when (dialog.show().tag) {
                CommandTag.OK -> {
                    try {
                        ServerPlugin.startServer(
                            dialog.tcpPortField.text.toInt(),
                            dialog.udpPortField.text.toInt()
                        )

                        if (dialog.connectCheckbox.isSelected) {
                            try {
                                ServerPlugin.connectServer(
                                    dialog.timeoutField.text.toInt(),
                                    "localhost",
                                    dialog.tcpPortField.text.toInt(),
                                    dialog.udpPortField.text.toInt()
                                )
                            } catch (error: IOException) {
                                ServerPlugin.logger.warn("Failed to connect to TCP: ${dialog.tcpPortField.text}")
                            }
                        }
                    } catch (error: IOException) {
                        ServerPlugin.logger.warn("Failed to start a server for TCP: ${dialog.tcpPortField.text}")
                    }
                }
                else -> {
                }
            }
        }
    }

    private val timeoutField = JXTextField("Timeout").apply { text = "5000" }
    private val tcpPortField = JXTextField("TCP Port").apply { text = "50000" }
    private val udpPortField = JXTextField("UDP Port").apply { text = "50000" }
    private val connectCheckbox = JCheckBox("Connect", true).apply { isOpaque = false }

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = JPanel().apply {
            isOpaque = false
            layout = GridBagLayout()

            preferredSize = Dimension(200, 20)

            add(JXLabel("TCP Port:"), StickEast)
            add(tcpPortField, FillHorizontalFinishLine)

            add(JXLabel("UDP Port:"), StickEast)
            add(udpPortField, FillHorizontalFinishLine)

            add(JXLabel("Timeout:"), StickEast)
            add(timeoutField, FillHorizontalFinishLine)

            add(JXLabel())
            add(connectCheckbox, StickEastFinishLine)
        }
    }
}