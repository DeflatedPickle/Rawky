package com.deflatedpickle.rawky.server.backend.util.user

import com.deflatedpickle.marvin.Colour
import java.awt.Point

interface IUser {
    companion object {
        val originPoint = Point(0, 0)
    }

    val id: Int
    val userName: String
    val mousePosition: Point
    val colour: Colour
}