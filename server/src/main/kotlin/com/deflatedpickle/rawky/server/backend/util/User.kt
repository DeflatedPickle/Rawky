package com.deflatedpickle.rawky.server.backend.util

import com.deflatedpickle.marvin.Colour
import com.deflatedpickle.undulation.extensions.toColour
import java.awt.Color
import java.awt.Point

data class User(
    val id: Int = -1,
    var userName: String = "",
    val mousePosition: Point = originPoint,
    val colour: Colour = Color.BLACK.toColour(),
) {
    companion object {
        val originPoint = Point(0, 0)
    }
}