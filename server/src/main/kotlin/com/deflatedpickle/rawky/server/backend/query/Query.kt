package com.deflatedpickle.rawky.server.backend.query

import com.deflatedpickle.rawky.server.backend.api.packet.ClientPacket
import com.deflatedpickle.rawky.server.backend.api.packet.ServerPacket

/**
 * Queries can be sent both to the server and to the client
 */
abstract class Query : ServerPacket, ClientPacket