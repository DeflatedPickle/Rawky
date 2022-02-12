package com.deflatedpickle.rawky.server.backend.query

import com.deflatedpickle.marvin.Colour
import com.deflatedpickle.rawky.collection.Cell

data class QueryChangeColour(
    val id: Int = -1,
    val colour: Colour = Cell.defaultColour,
) : Query()