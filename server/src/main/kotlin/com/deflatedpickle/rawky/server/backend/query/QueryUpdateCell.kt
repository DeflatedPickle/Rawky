package com.deflatedpickle.rawky.server.backend.query

import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.response.Response
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server

data class QueryUpdateCell(
    val cell: Cell? = null,
) : Query() {
    override fun runServer(connection: Connection, server: Server) {
        ServerPlugin.updateCell(this)
    }

    override fun runClient(connection: Connection, client: Client) {
        ServerPlugin.updateCell(this)
    }
}