package com.deflatedpickle.rawky.server.backend.util

import java.awt.Point

data class User(
    val id: Int = -1,
    val userName: String = "",
    val mousePosition: Point = originPoint
) {
    companion object {
        val originPoint = Point(0, 0)
    }
}