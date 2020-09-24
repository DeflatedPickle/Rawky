package com.deflatedpickle.rawky.server.backend.response

import java.awt.Point
import java.util.*

data class ResponseMoveMouse(
    override val id: Int = -1,
    val point: Point? = null
) : Response()