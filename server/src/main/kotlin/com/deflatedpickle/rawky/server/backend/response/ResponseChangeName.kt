/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.response

import com.deflatedpickle.rawky.server.backend.event.EventUserRename
import com.deflatedpickle.rawky.server.backend.util.User
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection

data class ResponseChangeName(
    override val id: Int = -1,
    val deadName: String = "",
    val realName: String = "",
) : Response() {
    override fun runClient(connection: Connection, client: Client) {
        EventUserRename.trigger(User(id, realName))
    }
}
