package com.deflatedpickle.rawky

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.api.plugin.PluginType

@Plugin(
    value = "core",
    author = "DeflatedPickle",
    description = """
        The core program
        <br>
        This provides the whole API for Rawky.
    """,
    type = PluginType.CORE_API
)
object Core