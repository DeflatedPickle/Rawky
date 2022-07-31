package com.deflatedpickle.rawky.autosave.util

import com.deflatedpickle.rawky.api.impex.Exporter

data class FileType(
    val handler: Exporter,
    val extension: String,
)