/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.server.encoding.base58

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.server.backend.api.Encoder
import com.deflatedpickle.rawky.server.backend.api.Encoder.Companion.registry
import io.github.novacrypto.base58.Base58

@Plugin(
    value = "base58",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        An encoder for Base58
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@server#*",
    ],
)
object Base58Plugin : Encoder("Base58") {
    override fun encode(text: ByteArray): String = Base58.base58Encode(text)

    override fun decode(text: String): ByteArray = Base58.base58Decode(text)

    init {
        registry[name] = this
    }
}
