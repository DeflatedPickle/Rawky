/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.request

import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.ServerPlugin.server
import com.deflatedpickle.rawky.server.backend.response.ResponseActiveUsers
import com.deflatedpickle.rawky.server.backend.response.ResponseUserJoin
import com.deflatedpickle.rawky.server.backend.util.User
import com.deflatedpickle.rawky.server.backend.util.UserUpdate.JOIN
import com.deflatedpickle.rawky.server.frontend.widget.ServerPanel
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server

data class RequestUserJoin(val userName: String = "") : Request() {
    override fun runServer(connection: Connection, server: Server) {
        ServerPlugin.logger.info("$userName joined")

        ServerPlugin.userMap[connection.id] = User(id = connection.id, userName = userName)

        server.apply {
            sendToAllTCP(
                ResponseActiveUsers(
                    ServerPlugin.userMap,
                    JOIN,
                ),
            )

            sendToAllTCP(
                ResponseUserJoin(
                    connection.id,
                    userName,
                ),
            )
        }

        RawkyPlugin.document?.let { doc ->
            ServerPanel.sendGrid(doc)

            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]
            val grid = layer.child

            for (i in grid.children) {
                // TODO
        /*if (i.colour != Cell.defaultColour) {
            server.sendToAllExceptTCP(ServerPlugin.id, QueryUpdateCell(i))
        }*/
            }
        }
    }
}
