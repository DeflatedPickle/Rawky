/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.server.frontend.dialog

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.ServerSettings
import com.deflatedpickle.rawky.server.backend.api.Encoder
import com.deflatedpickle.rawky.server.backend.util.functions.extension.get
import com.deflatedpickle.rawky.server.backend.util.functions.ipFromByteArray
import com.deflatedpickle.rawky.server.backend.util.functions.portFromByteArray
import com.deflatedpickle.rawky.server.frontend.widget.EncoderComboBox
import com.deflatedpickle.rawky.server.frontend.widget.form
import com.deflatedpickle.undulation.DocumentAdapter
import com.deflatedpickle.undulation.builder.ProgressMonitorBuilder
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel
import javax.swing.SwingUtilities

class DialogConnectServer : TaskDialog(Haruhi.window, "Connect to Server") {
    companion object {
        fun open() {
            val dialog = DialogConnectServer()

            when (dialog.show().tag) {
                CommandTag.OK -> {
                    ProgressMonitorBuilder(Haruhi.window)
                        .title("Connecting to Server")
                        .queue {
                            note = "Decoding security code"
                            task = {
                                (dialog.encodingComboBox.selectedItem as Encoder)
                                    .decode(dialog.securityCodeField.text)
                            }
                        }
                        .queue {
                            note = "Decoding IP and ports from the security code"
                            task = {
                                val decoded = it as ByteArray

                                // 00111100111100111100
                                val ipAddress = ipFromByteArray(decoded[2..6])
                                val tcpPort = portFromByteArray(decoded[8..12])
                                val udpPort = portFromByteArray(decoded[14..18])

                                // println("IP: $ipAddress\nTCP: $tcpPort\nUDP: $udpPort")

                                Triple(ipAddress, tcpPort, udpPort)
                            }
                        }
                        .queuwu {
                            task = { pm, a ->
                                val triple = a as Triple<String, Int, Int>

                                ServerPlugin.connectServer(
                                    dialog.timeoutField.value as Int,
                                    triple.first,
                                    triple.second,
                                    triple.third,
                                    dialog.userNameField.text,
                                    pm,
                                    dialog.retriesFiled.value as Int,
                                )
                            }
                        }
                        .queue {
                            note = "Finished connecting to server"
                        }
                        .build()
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
        ConfigUtil.getSettings<ServerSettings>("deflatedpickle@server#*")?.let {
            text = it.defaultUserName
        }

        this.document.addDocumentListener(
            DocumentAdapter {
                fireValidationFinished(validationCheck())
            }
        )
    }

    // Connection
    private val securityCodeField = JXTextField("Session Code").apply {
        this.document.addDocumentListener(
            DocumentAdapter {
                fireValidationFinished(validationCheck())
            }
        )
    }
    private val encodingComboBox = EncoderComboBox()
    private val timeoutField = JSpinner(SpinnerNumberModel(5000, 0, 5000 * 5, 5))
    private val retriesFiled = JSpinner(SpinnerNumberModel(10, 0, 100, 1))

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        SwingUtilities.invokeLater {
            securityCodeField.requestFocus()
            fireValidationFinished(validationCheck())
        }

        this.fixedComponent = form {
            category("Details")
            widget("Username", userNameField)

            category("Connection")
            widget("Session Code", securityCodeField)
            widget("Encoding", encodingComboBox)
            widget("Timeout", timeoutField)
            widget("Retries", retriesFiled)
        }
    }
}
