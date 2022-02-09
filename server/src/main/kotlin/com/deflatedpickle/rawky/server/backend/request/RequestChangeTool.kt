package com.deflatedpickle.rawky.server.backend.request

import com.deflatedpickle.rawky.api.Tool

data class RequestChangeTool(
    val id: Int = -1,
    val oldTool: Tool? = null,
    val newTool: Tool? = null,
) : Request()