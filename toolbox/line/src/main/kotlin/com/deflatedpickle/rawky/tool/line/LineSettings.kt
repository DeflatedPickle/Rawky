package com.deflatedpickle.rawky.tool.line

import com.deflatedpickle.haruhi.api.Config
import com.deflatedpickle.rawky.tool.line.api.Mode
import com.deflatedpickle.rawky.tool.line.api.Mode.SINGLE
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class LineSettings(
    override val version: Int = 1,
    var mode: Mode = SINGLE,
) : Config
