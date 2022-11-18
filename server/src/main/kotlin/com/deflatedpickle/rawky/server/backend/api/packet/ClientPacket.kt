/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.api.packet

import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection

interface ClientPacket : Packet {
    fun runClient(connection: Connection, client: Client)
}
