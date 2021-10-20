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
import com.deflatedpickle.undulation.constraints.StickEast
import com.deflatedpickle.undulation.constraints.StickEastFinishLine
import com.dosse.upnp.UPnP
import com.github.fzakaria.ascii85.Ascii85
import org.apache.logging.log4j.LogManager
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.IOException
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JSpinner
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
                    try {
                        val tcp = dialog.tcpPortField.value as Int
                        val udp = dialog.udpPortField.value as Int

                        if (dialog.uPnPCheckBox.isSelected) {
                            ServerPlugin.upnpStart(tcp, udp)
                        }

                        ServerPlugin.startServer(
                            tcp,
                            udp
                        )

                        val ipByteArray = ipToByteArray(getPublicIP())
                        val tcpPortByteArray = portToByteArray(tcp)
                        val udpPortByteArray = portToByteArray(udp)

                        // 00111100111100111100
                        val securityCodeByteArray = byteArrayOf(
                            *Random.nextBytes(2),
                            *ipByteArray,
                            *Random.nextBytes(2),
                            *tcpPortByteArray,
                            *Random.nextBytes(2),
                            *udpPortByteArray,
                            *Random.nextBytes(2)
                        )

                        val securityCode = when (dialog.encodingComboBox.selectedItem as Encoding) {
                            Encoding.ASCII85 -> {
                                Ascii85.encode(securityCodeByteArray)
                            }
                        }

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

                        if (dialog.connectCheckbox.isSelected) {
                            try {
                                ServerPlugin.connectServer(
                                    dialog.timeoutField.value as Int,
                                    "localhost",
                                    dialog.tcpPortField.value as Int,
                                    dialog.udpPortField.value as Int,
                                    dialog.userNameField.text
                                )
                            } catch (error: IOException) {
                                logger.warn(error)
                            }
                        }
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
        selectedItem = Encoding.values().last()
    }
    private val uPnPCheckBox = JCheckBox("UPnP", true).apply { isOpaque = false }
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
