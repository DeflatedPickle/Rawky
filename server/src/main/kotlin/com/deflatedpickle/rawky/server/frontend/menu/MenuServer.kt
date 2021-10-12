package com.deflatedpickle.rawky.server.frontend.menu

import com.deflatedpickle.rawky.server.frontend.dialog.DialogConnectServer
import com.deflatedpickle.rawky.server.frontend.dialog.DialogStartServer
import com.deflatedpickle.undulation.extensions.add
import javax.swing.JMenu

object MenuServer : JMenu("Server") {
    init {
        this.add("Start a Server") { DialogStartServer.open() }
        this.add("Connect to Server") { DialogConnectServer.open() }
    }
}