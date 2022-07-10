package com.deflatedpickle.rawky.server.chat.query

import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.query.Query
import com.deflatedpickle.rawky.server.chat.ChatPanel
import com.deflatedpickle.rawky.server.chat.Message
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server

data class QuerySendChat(
    val id: Int = -1,
    val message: String = "",
) : Query() {
    override fun runServer(connection: Connection, server: Server) {
        server.sendToAllTCP(this)
    }

    override fun runClient(connection: Connection, client: Client) {
        ChatPanel.model.addElement(
            Message(
                ServerPlugin.userMap[id],
                message,
            )
        )
    }
}