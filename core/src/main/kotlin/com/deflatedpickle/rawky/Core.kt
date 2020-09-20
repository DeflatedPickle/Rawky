package com.deflatedpickle.rawky

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.setting.RawkyDocument

// This plugin only exists to be a dependency
@Plugin(
    value = "core",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        The core program
        <br>
        This provides the whole API for Rawky.
    """,
    type = PluginType.CORE_API
)
@Suppress("unused")
object Core {
    var document: RawkyDocument? = null
}