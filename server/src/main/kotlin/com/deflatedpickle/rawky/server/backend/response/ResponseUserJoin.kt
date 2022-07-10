package com.deflatedpickle.rawky.server.backend.response

import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.event.EventUserJoinServer
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection

data class ResponseUserJoin(
    override val id: Int = -1,
    val userName: String = "",
) : Response() {
    override fun runClient(connection: Connection, client: Client) {
        ServerPlugin.userMap[id]?.let {
            // logger.info("${it.userName} joined")
            it.let { EventUserJoinServer.trigger(it) }
        }
    }
}