/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.server.encoding.base62

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.server.backend.api.Encoder
import com.deflatedpickle.rawky.server.backend.api.Encoder.Companion.registry
import io.seruco.encoding.base62.Base62

@Plugin(
    value = "base62",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        An encoder for Base62
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@server#*",
    ],
)
object Base62Plugin : Encoder("Base62") {
    override fun encode(text: ByteArray): String =
        Base62.createInstance().encode(text).decodeToString()

    override fun decode(text: String): ByteArray = Base62.createInstance().decode(text.toByteArray())

    init {
        registry[name] = this
    }
}
