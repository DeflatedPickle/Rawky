package com.deflatedpickle.rawky.server.frontend.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.ui.constraints.StickEast
import com.deflatedpickle.tosuto.constraints.FillHorizontalFinishLine
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Dimension
import java.awt.GridBagLayout
import java.io.IOException
import javax.swing.JPanel

class DialogConnectServer : TaskDialog(PluginUtil.window, "Connect to Server") {
    companion object {
        fun open() {
            val dialog = DialogConnectServer()

            when (dialog.show().tag) {
                CommandTag.OK -> {
                    try {
                        ServerPlugin.connectServer(
                            dialog.timeoutField.text.toInt(),
                            dialog.ipAddressField.text,
                            dialog.tcpPortField.text.toInt(),
                            dialog.udpPortField.text.toInt()
                        )
                    } catch (error: IOException) {
                        ServerPlugin.logger.warn("Failed to connect to IP: ${dialog.ipAddressField.text}, TCP: ${dialog.tcpPortField.text.toInt()}")
                    }
                }
                else -> {
                }
            }
        }
    }

    private val timeoutField = JXTextField("Timeout").apply { text = "5000" }
    private val ipAddressField = JXTextField("IP Address").apply { text = "localhost" }
    private val tcpPortField = JXTextField("TCP Port").apply { text = "50000" }
    private val udpPortField = JXTextField("UDP Port").apply { text = "50000" }

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = JPanel().apply {
            isOpaque = false
            layout = GridBagLayout()

            preferredSize = Dimension(200, 20)

            add(JXLabel("IP Address:"), StickEast)
            add(ipAddressField, FillHorizontalFinishLine)

            add(JXLabel("TCP Port:"), StickEast)
            add(tcpPortField, FillHorizontalFinishLine)

            add(JXLabel("UDP Port:"), StickEast)
            add(udpPortField, FillHorizontalFinishLine)

            add(JXLabel("Timeout:"), StickEast)
            add(timeoutField, FillHorizontalFinishLine)
        }
    }
}