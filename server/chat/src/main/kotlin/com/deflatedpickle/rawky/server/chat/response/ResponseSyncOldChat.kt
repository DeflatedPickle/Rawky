/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.chat.response

import com.deflatedpickle.rawky.server.backend.response.Response
import com.deflatedpickle.rawky.server.chat.ChatPanel
import com.deflatedpickle.rawky.server.chat.Message
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection

data class ResponseSyncOldChat(
    val messages: List<Message> = listOf(),
) : Response() {
    override fun runClient(connection: Connection, client: Client) {
        for (i in messages) {
            ChatPanel.model.addElement(i)
        }
    }
}
