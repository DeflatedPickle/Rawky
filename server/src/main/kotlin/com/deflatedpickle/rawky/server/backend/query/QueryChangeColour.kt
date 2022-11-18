/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.query

import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.ServerPlugin.server
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server
import java.awt.Color

data class QueryChangeColour(
    val id: Int = -1,
    val colour: Color = Cell.defaultColour,
) : Query() {
    override fun runServer(connection: Connection, server: Server) {
        server.sendToAllTCP(this)
    }

    override fun runClient(connection: Connection, client: Client) {
        ServerPlugin.userMap[id]?.colour = colour
    }
}
