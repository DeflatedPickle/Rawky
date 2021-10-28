@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.server.frontend.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.util.Encoding
import com.deflatedpickle.rawky.server.backend.util.functions.extension.get
import com.deflatedpickle.rawky.server.backend.util.functions.ipToByteArray
import com.deflatedpickle.rawky.server.backend.util.functions.getPublicIP
import com.deflatedpickle.rawky.server.backend.util.functions.ipFromByteArray
import com.deflatedpickle.rawky.server.backend.util.functions.portFromByteArray
import com.deflatedpickle.rawky.server.backend.util.functions.portToByteArray
import com.deflatedpickle.rawky.server.frontend.widget.form
import com.deflatedpickle.tosuto.ToastItem
import com.deflatedpickle.tosuto.action.ToastSingleAction
import com.deflatedpickle.undulation.DocumentAdapter
import com.deflatedpickle.undulation.builder.ProgressMonitorBuilder
import com.deflatedpickle.undulation.constraints.StickEast
import com.deflatedpickle.undulation.constraints.StickEastFinishLine
import com.dosse.upnp.UPnP
import com.github.fzakaria.ascii85.Ascii85
import com.github.underscore.lodash.Base32
import de.bwaldvogel.base91.Base91
import io.github.novacrypto.base58.Base58
import io.seruco.encoding.base62.Base62
import org.apache.logging.log4j.LogManager
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.IOException
import java.util.*
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JSpinner
import javax.swing.ProgressMonitor
import javax.swing.SpinnerNumberModel
import javax.swing.SwingUtilities
import kotlin.random.Random

class DialogStartServer : TaskDialog(PluginUtil.window, "Start a Server") {
    companion object {
        private val logger = LogManager.getLogger()

        fun open() {
            val dialog = DialogStartServer()

            when (dialog.show()?.tag) {
                CommandTag.OK -> {
                    ProgressMonitorBuilder(PluginUtil.window)
                        .title("Starting a Server")
                        .queue {
                            note = "Starting UPnP connection"
                            task = {
                                if (dialog.uPnPCheckBox.isSelected) {
                                    ServerPlugin.upnpStart(
                                        dialog.tcpPortField.value as Int,
                                        dialog.udpPortField.value as Int,
                                    )
                                }
                            }
                        }
                        .queue {
                            note = "Starting server"
                            task = {
                                ServerPlugin.startServer(
                                    dialog.tcpPortField.value as Int,
                                    dialog.udpPortField.value as Int
                                )
                            }
                        }
                        .queue {
                            note = "Encoding IP and ports"
                            task = {
                                val ipByteArray = ipToByteArray(getPublicIP())
                                val tcpPortByteArray = portToByteArray(dialog.tcpPortField.value as Int)
                                val udpPortByteArray = portToByteArray(dialog.udpPortField.value as Int)

                                Triple(ipByteArray, tcpPortByteArray, udpPortByteArray)
                            }
                        }
                        .queue {
                            note = "Constructing array"
                            task = {
                                val triple = it as Triple<ByteArray, ByteArray, ByteArray>

                                val securityCodeByteArray = byteArrayOf(
                                    *Random.nextBytes(2),
                                    *triple.first,
                                    *Random.nextBytes(2),
                                    *triple.second,
                                    *Random.nextBytes(2),
                                    *triple.third,
                                    *Random.nextBytes(2)
                                )

                                securityCodeByteArray
                            }
                        }
                        .queue {
                            note = "Encoding security code"
                            task = {
                                val securityCodeByteArray = it as ByteArray

                                when (dialog.encodingComboBox.selectedItem as Encoding) {
                                    Encoding.BASE91 -> Base91.encode(securityCodeByteArray).decodeToString()
                                    Encoding.ASCII85 -> Ascii85.encode(securityCodeByteArray)
                                    Encoding.BASE64 -> Base64.getEncoder().encodeToString(securityCodeByteArray)
                                    Encoding.BASE62 -> Base62.createInstance().encode(securityCodeByteArray).decodeToString()
                                    Encoding.BASE58 -> Base58.base58Encode(securityCodeByteArray)
                                }// .also { println(it) }
                            }
                        }
                        .queue {
                            note = "Making toast"
                            task = {
                                val securityCode = it as String

                                PluginUtil.toastWindow.addToast(
                                    ToastItem(
                                        title = "Session Code",
                                        content = securityCode,
                                        actions = listOf(
                                            ToastSingleAction(
                                                text = "Copy",
                                                command = { _, _ ->
                                                    Toolkit
                                                        .getDefaultToolkit()
                                                        .systemClipboard
                                                        .setContents(
                                                            StringSelection(securityCode),
                                                            null
                                                        )
                                                }
                                            )
                                        )
                                    )
                                )
                            }
                        }
                        .queuwu {
                            task = { pm, _ ->
                                if (dialog.connectCheckbox.isSelected) {
                                    try {
                                        ServerPlugin.connectServer(
                                            dialog.timeoutField.value as Int,
                                            "localhost",
                                            dialog.tcpPortField.value as Int,
                                            dialog.udpPortField.value as Int,
                                            dialog.userNameField.text,
                                            pm,
                                            null,
                                        )
                                    } catch (error: IOException) {
                                        logger.warn(error)
                                    }
                                }
                            }
                        }
                        .build()
                }
                else -> {
                }
            }
        }
    }

    private fun validationCheck(): Boolean =
        userNameField.text.isNotBlank()

    // Details
    private val userNameField = JXTextField("Username").apply {
        text = "Host"

        this.document.addDocumentListener(DocumentAdapter {
            fireValidationFinished(validationCheck())
        })
    }

    private val portMin = 49_152
    private val portMax = 65_535

    // Connection
    private val timeoutField = JSpinner(SpinnerNumberModel(5000, 0, 5000 * 5, 5))
    private val tcpPortField = JSpinner(SpinnerNumberModel(50000, portMin, portMax, 1))
    private val udpPortField = JSpinner(SpinnerNumberModel(50000, portMin, portMax, 1))
    private val encodingComboBox = JComboBox(Encoding.values()).apply {
        selectedItem = Encoding.ASCII85
    }
    private val uPnPCheckBox = JCheckBox("UPnP", UPnP.isUPnPAvailable()).apply {
        isOpaque = false
        isEnabled = UPnP.isUPnPAvailable()
    }
    private val connectCheckbox = JCheckBox("Connect", true).apply { isOpaque = false }

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        SwingUtilities.invokeLater {
            fireValidationFinished(validationCheck())
        }

        this.fixedComponent = form {
            category("Details")
            widget("Username", userNameField)

            category("Connection")
            widget("TCP Port", tcpPortField)
            widget("UDP Port", udpPortField)
            widget("Encoding", encodingComboBox)
            widget("Timeout", timeoutField)
            add(uPnPCheckBox, StickEast)
            add(connectCheckbox, StickEastFinishLine)
        }
    }
}
