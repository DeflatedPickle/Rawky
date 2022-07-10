package com.deflatedpickle.rawky.server.backend.response

import com.deflatedpickle.rawky.server.backend.api.packet.ClientPacket

/**
 * Responses come from the server
 */
abstract class Response(
    open val id: Int = -1
) : ClientPacket