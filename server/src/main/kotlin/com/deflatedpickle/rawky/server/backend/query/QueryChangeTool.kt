package com.deflatedpickle.rawky.server.backend.query

import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.server.backend.request.Request

data class QueryChangeTool(
    val id: Int = -1,
    val oldTool: Tool? = null,
    val newTool: Tool? = null,
) : Request()