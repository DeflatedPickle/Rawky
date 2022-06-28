package com.deflatedpickle.rawky.api.impex

import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.rawky.api.HasExtension
import com.deflatedpickle.rawky.api.HasName
import com.deflatedpickle.rawky.api.HasRegistry
import com.deflatedpickle.rawky.setting.RawkyDocument
import java.io.File

interface Exporter : HasName, HasExtension {
    companion object : HasRegistry<String, Exporter> {
        override val registry = Registry<String, Exporter>()
    }

    /**
     * Writes the current document to a file
     */
    fun export(doc: RawkyDocument, file: File)
}