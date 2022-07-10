package com.deflatedpickle.rawky.server.backend.response

import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.event.EventUserLeaveServer
import com.deflatedpickle.rawky.server.backend.request.RequestRemoveUser
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection

data class ResponseUserLeave(
    override val id: Int = -1,
) : Response() {
    override fun runClient(connection: Connection, client: Client) {
        ServerPlugin.userMap[id]?.let {
            ServerPlugin.logger.info("${it.userName} left")
            it.let { EventUserLeaveServer.trigger(it) }
            ServerPlugin.client.sendTCP(RequestRemoveUser())
        }
    }
}