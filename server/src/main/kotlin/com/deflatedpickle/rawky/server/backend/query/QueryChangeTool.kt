/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.query

import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.server.ServerPlugin
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server

data class QueryChangeTool(
    val id: Int = -1,
    val oldTool: Tool? = null,
    val newTool: Tool? = null,
) : Query() {
    override fun runServer(connection: Connection, server: Server) {
        server.sendToAllTCP(this)
    }

    override fun runClient(connection: Connection, client: Client) {
        newTool?.let { tool -> ServerPlugin.userMap[id]?.tool = tool }
    }
}
