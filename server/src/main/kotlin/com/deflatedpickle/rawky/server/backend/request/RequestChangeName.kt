/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.request

import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.ServerPlugin.server
import com.deflatedpickle.rawky.server.backend.response.ResponseActiveUsers
import com.deflatedpickle.rawky.server.backend.response.ResponseChangeName
import com.deflatedpickle.rawky.server.backend.util.UserUpdate.RENAME
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server

data class RequestChangeName(
    val id: Int = -1,
    val deadName: String = "",
    val realName: String = "",
) : Request() {
    override fun runServer(connection: Connection, server: Server) {
        ServerPlugin.logger.info("User $id requested to change their name to $realName")

        server.apply {
            sendToAllTCP(
                ResponseActiveUsers(
                    ServerPlugin.userMap.also { it[id]!!.userName = realName },
                    RENAME,
                ),
            )

            sendToAllTCP(
                ResponseChangeName(
                    connection.id,
                    deadName,
                    realName,
                ),
            )
        }
    }
}
