/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.response

import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.util.JoinFail
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection

data class ResponseJoinFail(val reason: JoinFail = JoinFail.UNSPECIFIED) : Response() {
    override fun runClient(connection: Connection, client: Client) {
        ServerPlugin.logger.warn("Failed to join due to $reason")
    }
}
