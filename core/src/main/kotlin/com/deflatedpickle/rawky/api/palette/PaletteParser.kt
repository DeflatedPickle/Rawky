/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.palette

import com.deflatedpickle.rawky.api.HasName
import java.io.File

interface PaletteParser<T> : HasName {
    fun parse(file: File): Palette<T>
}
