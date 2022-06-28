package com.deflatedpickle.rawky.server.backend.query

import com.deflatedpickle.rawky.collection.Cell
import java.awt.Color

data class QueryChangeColour(
    val id: Int = -1,
    val colour: Color = Cell.defaultColour,
) : Query()