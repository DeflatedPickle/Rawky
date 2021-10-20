package com.deflatedpickle.rawky.server.backend.query

import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.server.backend.response.Response

data class QueryUpdateCell(
    val cell: Cell? = null,
) : Query()