@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.server.frontend.dialog

import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.ServerSettings
import com.deflatedpickle.rawky.server.backend.util.Encoding
import com.deflatedpickle.rawky.server.backend.util.functions.extension.get
import com.deflatedpickle.rawky.server.backend.util.functions.ipFromByteArray
import com.deflatedpickle.rawky.server.backend.util.functions.portFromByteArray
import com.deflatedpickle.rawky.server.frontend.widget.form
import com.deflatedpickle.undulation.DocumentAdapter
import com.deflatedpickle.undulation.builder.ProgressMonitorBuilder
import com.github.fzakaria.ascii85.Ascii85
import com.github.underscore.lodash.Base32
import de.bwaldvogel.base91.Base91
import io.github.novacrypto.base58.Base58
import io.seruco.encoding.base62.Base62
import org.apache.logging.log4j.LogManager
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.io.IOException
import java.util.*
import javax.swing.JComboBox
import javax.swing.JSpinner
import javax.swing.ProgressMonitor
import javax.swing.SpinnerNumberModel
import javax.swing.SwingUtilities

class DialogConnectServer : TaskDialog(PluginUtil.window, "Connect to Server") {
    companion object {
        fun open() {
            val dialog = DialogConnectServer()

            when (dialog.show().tag) {
                CommandTag.OK -> {
                    ProgressMonitorBuilder(PluginUtil.window)
                        .title("Connecting to Server")
                        .queue {
                            note = "Decoding security code"
                            task = {
                                val text = dialog.securityCodeField.text

                                when (dialog.encodingComboBox.selectedItem as Encoding) {
                                    Encoding.BASE91 -> Base91.decode(text.toByteArray())
                                    Encoding.ASCII85 -> Ascii85.decode(text)
                                    Encoding.BASE64 -> Base64.getDecoder().decode(text)
                                    Encoding.BASE62 -> Base62.createInstance().decode(text.toByteArray())
                                    Encoding.BASE58 -> Base58.base58Decode(text)
                                }
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
    private val encodingComboBox = JComboBox(Encoding.values()).apply {
        ConfigUtil.getSettings<ServerSettings>("deflatedpickle@server#*")?.let {
            selectedItem = it.defaultConnectionEncoding
        }
    }
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