/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.impex

import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.rawky.api.HasName
import com.deflatedpickle.rawky.api.HasRegistry
import com.deflatedpickle.rawky.setting.RawkyDocument
import java.io.File

interface Opener : HasName {
    companion object : HasRegistry<String, Opener> {
        override val registry = Registry<String, Opener>()
    }

    val openerExtensions: MutableMap<String, List<String>>

    /**
     * Replaces the current document
     */
    fun open(file: File): RawkyDocument
}
