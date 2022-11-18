/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.rawky.server.backend.api

import com.deflatedpickle.marvin.registry.Registry

abstract class Encoder(
    val name: String
) {
    companion object {
        val registry = Registry<String, Encoder>()
    }

    abstract fun encode(text: ByteArray): String
    abstract fun decode(text: String): ByteArray

    override fun toString() = name
}
