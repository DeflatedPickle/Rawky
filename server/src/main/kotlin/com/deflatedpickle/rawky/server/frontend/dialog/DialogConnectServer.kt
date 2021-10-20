package com.deflatedpickle.rawky.server.frontend.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.util.Encoding
import com.deflatedpickle.rawky.server.backend.util.functions.extension.get
import com.deflatedpickle.rawky.server.backend.util.functions.ipFromByteArray
import com.deflatedpickle.rawky.server.backend.util.functions.portFromByteArray
import com.deflatedpickle.rawky.server.frontend.widget.form
import com.deflatedpickle.undulation.DocumentAdapter
import com.github.fzakaria.ascii85.Ascii85
import org.apache.logging.log4j.LogManager
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.io.IOException
import javax.swing.JComboBox
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel
import javax.swing.SwingUtilities

class DialogConnectServer : TaskDialog(PluginUtil.window, "Connect to Server") {
    companion object {
        private val logger = LogManager.getLogger()

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

                        // 00111100111100111100
                        val ipAddress = ipFromByteArray(decoded[2..6])
                        val tcpPort = portFromByteArray(decoded[8..12])
                        val udpPort = portFromByteArray(decoded[14..18])

                        ServerPlugin.connectServer(
                            dialog.timeoutField.value as Int,
                            ipAddress,
                            tcpPort,
                            udpPort,
                            dialog.userNameField.text
                        )
                    } catch (error: IOException) {
                        logger.warn(error)
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun validationCheck(): Boolean =
        userNameField.text.isNotBlank() && securityCodeField.text.isNotBlank()

    // Details
    private val userNameField = JXTextField("Username").apply {
        text = "User"

        this.document.addDocumentListener(DocumentAdapter {
            fireValidationFinished(validationCheck())
        })
    }

    // Connection
    private val securityCodeField = JXTextField("Session Code").apply {
        this.document.addDocumentListener(DocumentAdapter {
            fireValidationFinished(validationCheck())
        })
    }
    private val encodingComboBox = JComboBox(Encoding.values())
    private val timeoutField = JSpinner(SpinnerNumberModel(5000, 0, 5000 * 5, 5))

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        SwingUtilities.invokeLater {
            fireValidationFinished(validationCheck())
        }

        this.fixedComponent = form {
            category("Details")
            widget("Username", userNameField)

            category("Connection")
            widget("Session Code", securityCodeField)
            widget("Encoding", encodingComboBox)
            widget("Timeout", timeoutField)
        }
    }
}