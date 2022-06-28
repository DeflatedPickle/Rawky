package com.deflatedpickle.rawky.api.impex

import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.rawky.api.HasExtension
import com.deflatedpickle.rawky.api.HasName
import com.deflatedpickle.rawky.api.HasRegistry
import com.deflatedpickle.rawky.setting.RawkyDocument
import java.io.File

interface Importer : HasName, HasExtension {
    companion object : HasRegistry<String, Importer> {
        override val registry = Registry<String, Importer>()
    }

    /**
     * Opens a file into the current document
     */
    fun import(document: RawkyDocument, file: File)
}