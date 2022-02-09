@file:Suppress("JoinDeclarationAndAssignment")

package com.deflatedpickle.rawky.server.frontend.menu

import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.frontend.dialog.DialogConnectServer
import com.deflatedpickle.rawky.server.frontend.dialog.DialogStartServer
import com.deflatedpickle.undulation.functions.extensions.add
import javax.swing.JMenu
import javax.swing.JMenuItem

object MenuServer : JMenu("Server") {
    private val start: JMenuItem
    private val close: JMenuItem

    private val connect: JMenuItem
    private val leave: JMenuItem

    init {
        start = add("Start a Server") { DialogStartServer.open() }
        close = add("Close a Server") { ServerPlugin.closeServer() }
        addSeparator()
        connect = add("Connect to Server") { DialogConnectServer.open() }
        leave = add("Disconnect from Server") { ServerPlugin.leaveServer() }
    }

    override fun setPopupMenuVisible(b: Boolean) {
        super.setPopupMenuVisible(b)

        if (b) {
            start.isEnabled = ServerPlugin.server == null
            close.isEnabled = ServerPlugin.server != null

            connect.isEnabled = ServerPlugin.server == null && !ServerPlugin.client.isConnected
            leave.isEnabled = ServerPlugin.server == null && ServerPlugin.client.isConnected
        }
    }
}