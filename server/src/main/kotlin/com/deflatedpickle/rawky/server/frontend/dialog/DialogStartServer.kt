package com.deflatedpickle.rawky.server.frontend.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.frontend.widget.form
import com.deflatedpickle.tosuto.constraints.FillHorizontalFinishLine
import org.jdesktop.swingx.JXTextField
import org.jdesktop.swingx.JXTitledSeparator
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.IOException
import javax.swing.JCheckBox
import javax.swing.SwingUtilities

class DialogStartServer : TaskDialog(PluginUtil.window, "Start a Server") {
    companion object {
        fun open() {
            val dialog = DialogStartServer()

            when (dialog.show()?.tag) {
                CommandTag.OK -> {
                    try {
                        ServerPlugin.startServer(
                            dialog.tcpPortField.text.toInt(),
                            dialog.udpPortField.text.toInt()
                        )
                        ServerPlugin.hasPassword = dialog.passwordCheckbox.isSelected
                        ServerPlugin.password = dialog.serverPasswordField.text

                        if (dialog.connectCheckbox.isSelected) {
                            try {
                                ServerPlugin.connectServer(
                                    dialog.timeoutField.text.toInt(),
                                    "localhost",
                                    dialog.tcpPortField.text.toInt(),
                                    dialog.udpPortField.text.toInt(),
                                    dialog.serverPasswordField.text,
                                    "Host"
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

    // Details
    private val passwordCheckbox = JCheckBox("Password", false).apply { isOpaque = false }

    // Connection
    private val timeoutField = JXTextField("Timeout").apply { text = "5000" }
    private val tcpPortField = JXTextField("TCP Port").apply { text = "50000" }
    private val udpPortField = JXTextField("UDP Port").apply { text = "50000" }
    private val connectCheckbox = JCheckBox("Connect", true).apply { isOpaque = false }
    private val serverPasswordField = JXTextField("Password").apply { isEnabled = false }

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.passwordCheckbox.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                serverPasswordField.isEnabled = passwordCheckbox.isSelected
            }
        })

        this.fixedComponent = form {
            add(JXTitledSeparator("Details"), FillHorizontalFinishLine)
            addCheckbox(passwordCheckbox)

            add(JXTitledSeparator("Connection"), FillHorizontalFinishLine)
            addField("Password", serverPasswordField)
            addField("TCP Port", tcpPortField)
            addField("UDP Port", udpPortField)
            addField("Timeout", timeoutField)
            addCheckbox(connectCheckbox)
        }
    }
}