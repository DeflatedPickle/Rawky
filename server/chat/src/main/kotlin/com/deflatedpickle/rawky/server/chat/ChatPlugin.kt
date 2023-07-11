/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.server.chat

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.event.EventJoinServer
import com.deflatedpickle.rawky.server.backend.event.EventRegisterPackets
import com.deflatedpickle.rawky.server.chat.query.QueryDeleteChat
import com.deflatedpickle.rawky.server.chat.query.QuerySendChat
import com.deflatedpickle.rawky.server.chat.request.RequestSyncOldChat
import com.deflatedpickle.rawky.server.chat.response.ResponseSyncOldChat
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively

@Plugin(
    value = "chat",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A widget to chat to other users
    """,
    type = PluginType.COMPONENT,
    component = ChatPanel::class,
    dependencies =
    [
        "deflatedpickle@core#*",
        "deflatedpickle@server#*",
    ],
)
object ChatPlugin {
    init {
        EventRegisterPackets.addListener {
            with(it.first) {
                register(Message::class.java)
                register(ArrayList::class.java)

                register(QuerySendChat::class.java)
                register(QueryDeleteChat::class.java)

                register(RequestSyncOldChat::class.java)
                register(ResponseSyncOldChat::class.java)
            }
        }

        EventJoinServer.addListener { ServerPlugin.client.sendTCP(RequestSyncOldChat(ServerPlugin.id)) }

        EventChangeTheme.addListener { ChatPanel.updateUIRecursively() }
    }

    fun sendMessage(string: String) {
        ChatPanel.sendField.text = ""

        ServerPlugin.client.sendTCP(
            QuerySendChat(
                ServerPlugin.id,
                string,
            ),
        )
    }
}
