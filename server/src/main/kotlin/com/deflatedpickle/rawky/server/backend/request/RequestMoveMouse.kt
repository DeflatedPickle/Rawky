package com.deflatedpickle.rawky.server.backend.request

import java.awt.Point

data class RequestMoveMouse(
    val point: Point? = null
) : Request()