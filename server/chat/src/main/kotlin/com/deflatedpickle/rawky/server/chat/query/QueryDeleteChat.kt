package com.deflatedpickle.rawky.server.chat.query

import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.query.Query
import com.deflatedpickle.rawky.server.chat.ChatPanel
import com.deflatedpickle.rawky.server.chat.Message
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server

data class QueryDeleteChat(
    val id: Int = -1,
    val messages: List<Message> = listOf(),
) : Query() {
    override fun runServer(connection: Connection, server: Server) {
        if (ChatPanel.list.selectedValuesList.all { it.id == ServerPlugin.id } || ServerPlugin.server != null) {
            server.sendToAllTCP(this)
        }
    }

    override fun runClient(connection: Connection, client: Client) {
        for (i in messages) {
            ChatPanel.model.removeElement(i)
        }
    }
}