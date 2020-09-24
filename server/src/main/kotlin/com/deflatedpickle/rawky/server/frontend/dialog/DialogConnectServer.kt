package com.deflatedpickle.rawky.server.frontend.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.request.RequestUserJoin
import com.deflatedpickle.rawky.server.frontend.widget.form
import com.deflatedpickle.tosuto.constraints.FillHorizontalFinishLine
import org.jdesktop.swingx.JXTextField
import org.jdesktop.swingx.JXTitledSeparator
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.io.IOException

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
                            dialog.udpPortField.text.toInt(),
                            dialog.serverPasswordField.text,
                            dialog.userNameField.text
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

    private val userNameField = JXTextField("Username")
    private val ipAddressField = JXTextField("IP Address").apply { text = "localhost" }
    private val serverPasswordField = JXTextField("Password")
    private val tcpPortField = JXTextField("TCP Port").apply { text = "50000" }
    private val udpPortField = JXTextField("UDP Port").apply { text = "50000" }
    private val timeoutField = JXTextField("Timeout").apply { text = "5000" }

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = form {
            add(JXTitledSeparator("User"), FillHorizontalFinishLine)
            addField("Username", userNameField)

            add(JXTitledSeparator("Connection"), FillHorizontalFinishLine)
            addField("IP Address", ipAddressField)
            addField("Password", serverPasswordField)
            addField("TCP Port", tcpPortField)
            addField("UDP Port", udpPortField)
            addField("Timeout", timeoutField)
        }
    }
}