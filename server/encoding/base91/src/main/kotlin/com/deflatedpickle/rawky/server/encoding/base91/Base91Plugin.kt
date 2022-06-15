@file:Suppress("unused")

package com.deflatedpickle.rawky.server.encoding.base91

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.server.backend.api.Encoder
import com.deflatedpickle.rawky.server.backend.api.Encoder.Companion.registry
import de.bwaldvogel.base91.Base91

@Plugin(
    value = "base91",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        An encoder for Base91
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@server#*",
    ],
)
object Base91Plugin : Encoder("Base91") {
    override fun encode(text: ByteArray): String = Base91.encode(text).decodeToString()
    override fun decode(text: String): ByteArray = Base91.decode(text.toByteArray())

    init {
        registry[name] = this
    }
}