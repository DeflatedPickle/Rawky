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
import com.dosse.upnp.UPnP
import com.github.fzakaria.ascii85.Ascii85
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.IOException
import javax.swing.JCheckBox
import javax.swing.JComboBox
import kotlin.random.Random

class DialogStartServer : TaskDialog(PluginUtil.window, "Start a Server") {
    companion object {
        fun open() {
            val dialog = DialogStartServer()

            when (dialog.show()?.tag) {
                CommandTag.OK -> {
                    try {
                        val tcp = dialog.tcpPortField.text.toInt()
                        val udp = dialog.udpPortField.text.toInt()

                        ServerPlugin.startServer(
                            tcp,
                            udp
                        )

                        if (dialog.uPnPCheckBox.isSelected) {
                            ServerPlugin.logger.info("Attempting UPnP port forwarding")
                            if (UPnP.isUPnPAvailable()) {
                                // Close the ports if they're open
                                UPnP.closePortTCP(tcp)
                                UPnP.closePortUDP(udp)

                                when {
                                    UPnP.isMappedTCP(tcp) -> {
                                        ServerPlugin.logger.warn("The TCP port: $tcp, is already mapped")
                                    }
                                    UPnP.isMappedUDP(udp) -> {
                                        ServerPlugin.logger.warn("The UDP port: $udp, is already mapped")
                                    }
                                    UPnP.openPortTCP(tcp) -> {
                                        ServerPlugin.logger.info("The TCP port: $tcp, has been opened")
                                        UPnP.openPortUDP(udp)
                                        ServerPlugin.logger.info("The UDP port: $udp, has been opened")
                                    }
                                    else -> {
                                        ServerPlugin.logger.error("UPnP port forwarding failed")
                                    }
                                }
                            } else {
                                ServerPlugin.logger.warn("UPnP is not available on your network")
                            }
                        }

                        val ipByteArray = ipToByteArray(getPublicIP())
                        val tcpPortByteArray = portToByteArray(dialog.tcpPortField.text.toInt())
                        val udpPortByteArray = portToByteArray(dialog.udpPortField.text.toInt())

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
                                    dialog.timeoutField.text.toInt(),
                                    "localhost",
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

    private val uPnPCheckBox = JCheckBox("UPnP", true).apply { isOpaque = false }

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
            check(uPnPCheckBox)

            category("Connection")
            widget("TCP Port", tcpPortField)
            widget("UDP Port", udpPortField)
            widget("Timeout", timeoutField)
            check(connectCheckbox)
        }
    }
}
