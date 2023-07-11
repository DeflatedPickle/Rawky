/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.server.encoding.base64

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.server.backend.api.Encoder
import java.util.Base64

@Plugin(
    value = "base64",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        An encoder for Base64
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@server#*",
    ],
)
object Base64Plugin : Encoder("Base64") {
    override fun encode(text: ByteArray): String = Base64.getEncoder().encodeToString(text)

    override fun decode(text: String): ByteArray = Base64.getDecoder().decode(text)

    init {
        registry[name] = this
    }
}
