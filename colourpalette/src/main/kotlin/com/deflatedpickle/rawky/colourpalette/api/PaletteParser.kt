/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.colourpalette.api

import com.deflatedpickle.marvin.registry.Registry
import java.io.File

interface PaletteParser {
    companion object {
        val registry = Registry<String, PaletteParser>()
    }

    fun parse(file: File): Palette
}
