/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.query

import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.server.ServerPlugin
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server

data class QueryUpdateCell(
    val cell: Cell<out Any>? = null,
) : Query() {
    override fun runServer(connection: Connection, server: Server) {
        ServerPlugin.updateCell(this)
    }

    override fun runClient(connection: Connection, client: Client) {
        ServerPlugin.updateCell(this)
    }
}
