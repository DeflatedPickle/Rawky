/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.response

import com.deflatedpickle.rawky.server.ServerPlugin
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import java.awt.Point

data class ResponseMoveMouse(override val id: Int = -1, val point: Point? = null) : Response() {
    override fun runClient(connection: Connection, client: Client) {
        if (point != null) {
            ServerPlugin.userMap[id]?.mousePosition?.location = point
        }
    }
}
