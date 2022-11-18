/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.request

import com.deflatedpickle.rawky.server.ServerPlugin
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server

data class RequestRemoveUser(
    val id: Int = -1,
) : Request() {
    override fun runServer(connection: Connection, server: Server) {
        ServerPlugin.userMap.remove(connection.id)
    }
}
