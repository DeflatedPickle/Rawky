/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.request

import com.deflatedpickle.rawky.server.ServerPlugin.server
import com.deflatedpickle.rawky.server.backend.response.ResponseMoveMouse
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server
import java.awt.Point

data class RequestMoveMouse(
    val point: Point? = null,
    val drag: Boolean = false,
) : Request() {
    override fun runServer(connection: Connection, server: Server) {
        server.sendToAllTCP(
            ResponseMoveMouse(
                connection.id,
                point,
            )
        )
    }
}
