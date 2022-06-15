@file:Suppress("unused")

package com.deflatedpickle.rawky.server.encoding.ascii85

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.server.backend.api.Encoder
import com.deflatedpickle.rawky.server.backend.api.Encoder.Companion.registry
import com.github.fzakaria.ascii85.Ascii85

@Plugin(
    value = "ascii85",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        An encoder for Ascii85
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@server#*",
    ],
)
object Ascii85Plugin : Encoder("Ascii85") {
    override fun encode(text: ByteArray): String = Ascii85.encode(text)
    override fun decode(text: String): ByteArray = Ascii85.decode(text)

    init {
        registry[name] = this
    }
}