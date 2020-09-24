package com.deflatedpickle.rawky.server.frontend.menu

import com.deflatedpickle.rawky.server.frontend.dialog.DialogConnectServer
import com.deflatedpickle.rawky.server.frontend.dialog.DialogStartServer
import com.deflatedpickle.rawky.ui.extension.addItem
import javax.swing.JMenu

object MenuServer : JMenu("Server") {
    init {
        this.addItem("Start a Server") { DialogStartServer.open() }
        this.addItem("Connect to Server") { DialogConnectServer.open() }
    }
}