package com.deflatedpickle.rawky.server.frontend.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.util.Encoding
import com.deflatedpickle.rawky.server.backend.util.functions.extension.get
import com.deflatedpickle.rawky.server.backend.util.functions.ipFromByteArray
import com.deflatedpickle.rawky.server.backend.util.functions.portFromByteArray
import com.deflatedpickle.rawky.server.frontend.widget.form
import com.github.fzakaria.ascii85.Ascii85
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.io.IOException
import javax.swing.JComboBox

class DialogConnectServer : TaskDialog(PluginUtil.window, "Connect to Server") {
    companion object {
        fun open() {
            val dialog = DialogConnectServer()

            when (dialog.show().tag) {
                CommandTag.OK -> {
                    try {
                        val decoded = when (dialog.encodingComboBox.selectedItem as Encoding) {
                            Encoding.ASCII85 -> {
                                Ascii85.decode(dialog.securityCodeField.text)
                            }
                        }

                        val ipAddress = ipFromByteArray(decoded[0..4])
                        val tcpPort = portFromByteArray(decoded[4..8])
                        val udpPort = portFromByteArray(decoded[8..12])

                        ServerPlugin.connectServer(
                            dialog.timeoutField.text.toInt(),
                            ipAddress,
                            tcpPort,
                            udpPort,
                            dialog.userNameField.text
                        )
                    } catch (error: IOException) {
                        ServerPlugin.logger.warn(error)
                    }
                }
                else -> {
                }
            }
        }
    }

    // Details
    private val userNameField = JXTextField("Username")

    // Connection
    private val securityCodeField = JXTextField("Security Code")
    private val encodingComboBox = JComboBox<Encoding>(Encoding.values())
    private val timeoutField = JXTextField("Timeout").apply { text = "5000" }

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = form {
            category("Details")
            widget("Username", userNameField)

            category("Connection")
            widget("Security Code", securityCodeField)
            widget("Encoding", encodingComboBox)
            widget("Timeout", timeoutField)
        }
    }
}