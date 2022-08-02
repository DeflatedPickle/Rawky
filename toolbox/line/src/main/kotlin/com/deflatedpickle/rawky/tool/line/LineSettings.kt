package com.deflatedpickle.rawky.tool.line

import com.deflatedpickle.rawky.tool.line.api.Mode
import com.deflatedpickle.rawky.tool.line.api.Mode.SINGLE
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class LineSettings(
    @Required var mode: Mode = SINGLE,
)
