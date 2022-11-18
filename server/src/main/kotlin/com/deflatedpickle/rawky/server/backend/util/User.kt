/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.util

import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Cell
import java.awt.Color
import java.awt.Point

data class User(
    val id: Int = -1,
    var userName: String = "",
    var role: Role = Role.USER,
    val mousePosition: Point = originPoint,
    var colour: Color = Cell.defaultColour,
    var tool: Tool = Tool.registry.values.first()
) {
    companion object {
        val originPoint = Point(0, 0)
    }
}
