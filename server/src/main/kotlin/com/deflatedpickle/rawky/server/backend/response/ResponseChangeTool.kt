package com.deflatedpickle.rawky.server.backend.response

import com.deflatedpickle.rawky.api.Tool

data class ResponseChangeTool(
    override val id: Int = -1,
    val oldTool: Tool? = null,
    val newTool: Tool? = null,
) : Response()