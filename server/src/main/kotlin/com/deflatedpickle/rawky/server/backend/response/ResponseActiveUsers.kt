/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.response

import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.util.User
import com.deflatedpickle.rawky.server.backend.util.UserUpdate
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection

data class ResponseActiveUsers(
    val activeUsers: Map<Int, User> = emptyMap(),
    val reason: UserUpdate = UserUpdate.OTHER,
) : Response() {
    override fun runClient(connection: Connection, client: Client) {
        ServerPlugin.userMap.putAll(activeUsers)
    }
}
