/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.chat.request

import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.request.Request
import com.deflatedpickle.rawky.server.chat.ChatPanel
import com.deflatedpickle.rawky.server.chat.response.ResponseSyncOldChat
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server

data class RequestSyncOldChat(
    val id: Int = -1,
    val limit: Int = -1,
) : Request() {
    override fun runServer(connection: Connection, server: Server) {
        ServerPlugin.logger.info("Sending previous messages to $id")

        server.sendToTCP(id, ResponseSyncOldChat(ChatPanel.model.elements().toList()))
    }
}
