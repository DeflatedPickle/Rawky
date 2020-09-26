package com.deflatedpickle.rawky.server.frontend.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.util.Encoding
import com.deflatedpickle.rawky.server.backend.util.functions.portFromByteArray
import com.deflatedpickle.rawky.server.backend.util.functions.ipToByteArray
import com.deflatedpickle.rawky.server.backend.util.functions.getLocalIP
import com.deflatedpickle.rawky.server.backend.util.functions.portToByteArray
import com.deflatedpickle.rawky.server.frontend.widget.form
import com.deflatedpickle.rawky.server.backend.util.functions.extension.get
import com.deflatedpickle.rawky.server.backend.util.functions.ipFromByteArray
import com.deflatedpickle.tosuto.ToastItem
import com.deflatedpickle.tosuto.action.ToastSingleAction
import com.dosse.upnp.UPnP
import com.github.fzakaria.ascii85.Ascii85
import com.github.underscore.lodash.Base32
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.IOException
import java.util.*
import javax.swing.JCheckBox
import javax.swing.JComboBox

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

                        val ipByteArray = ipToByteArray(getLocalIP())
                        val tcpPortByteArray = portToByteArray(dialog.tcpPortField.text.toInt())
                        val udpPortByteArray = portToByteArray(dialog.udpPortField.text.toInt())

                        val securityCodeByteArray = byteArrayOf(
                            *ipByteArray,
                            *tcpPortByteArray,
                            *udpPortByteArray
                        )

                        val securityCode = when (dialog.encodingComboBox.selectedItem as Encoding) {
                            Encoding.ASCII85 -> {
                                Ascii85.encode(securityCodeByteArray)
                            }
                        }

                        PluginUtil.toastWindow.addToast(
                            ToastItem(
                                title = "Security Code",
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
                                    dialog.timeoutField.text.toInt(),
                                    getLocalIP(),
                                    dialog.tcpPortField.text.toInt(),
                                    dialog.udpPortField.text.toInt(),
                                    "Host"
                                )
                            } catch (error: IOException) {
                                ServerPlugin.logger.warn(error)
                            }
                        }
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
    private val encodingComboBox = JComboBox<Encoding>(Encoding.values()).apply {
        selectedItem = Encoding.values().last()
    }

    // Connection
    private val timeoutField = JXTextField("Timeout").apply { text = "5000" }
    private val tcpPortField = JXTextField("TCP Port").apply { text = "50000" }
    private val udpPortField = JXTextField("UDP Port").apply { text = "50000" }
    private val connectCheckbox = JCheckBox("Connect", true).apply { isOpaque = false }

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = form {
            category("Details")
            widget("Encoding", encodingComboBox)

            category("Connection")
            widget("TCP Port", tcpPortField)
            widget("UDP Port", udpPortField)
            widget("Timeout", timeoutField)
            check(connectCheckbox)
        }
    }
}
