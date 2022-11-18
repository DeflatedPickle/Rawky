/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.export.text.ascii.api

import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.rawky.api.HasName
import com.deflatedpickle.rawky.api.HasRegistry
import java.awt.Color

abstract class Palette(
    override val name: String,
) : HasName {
    companion object : HasRegistry<String, Palette> {
        override val registry = Registry<String, Palette>()
    }

    abstract fun char(x: Int, y: Int, colour: Color): String

    override fun toString() = name
}
