/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.impex

import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.rawky.api.HasName
import com.deflatedpickle.rawky.api.HasRegistry
import com.deflatedpickle.rawky.setting.RawkyDocument
import java.io.File

interface Exporter : HasName {
    companion object : HasRegistry<String, Exporter> {
        override val registry = Registry<String, Exporter>()
    }

    val exporterExtensions: MutableMap<String, List<String>>

    /**
     * Writes the current document to a file
     */
    fun export(doc: RawkyDocument, file: File)
}
