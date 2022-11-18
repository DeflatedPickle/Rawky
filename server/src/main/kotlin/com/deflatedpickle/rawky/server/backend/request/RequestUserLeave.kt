/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.request

import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.ServerPlugin.server
import com.deflatedpickle.rawky.server.backend.response.ResponseActiveUsers
import com.deflatedpickle.rawky.server.backend.response.ResponseUserLeave
import com.deflatedpickle.rawky.server.backend.util.UserUpdate.LEAVE
import com.deflatedpickle.rawky.server.frontend.widget.ServerPanel
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server

data class RequestUserLeave(
    val id: Int = -1,
    val userName: String = "",
) : Request() {
    override fun runServer(connection: Connection, server: Server) {
        ServerPlugin.logger.info("${ServerPlugin.userMap[connection.id]!!.userName} left")

        server.sendToAllTCP(
            ResponseUserLeave(
                connection.id,
            )
        )

        ServerPanel.repaint()

        server.sendToAllTCP(
            ResponseActiveUsers(
                ServerPlugin.userMap,
                LEAVE,
            )
        )
    }
}
