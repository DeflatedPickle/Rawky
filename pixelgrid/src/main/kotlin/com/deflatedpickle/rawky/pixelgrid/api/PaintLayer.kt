/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.api

import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.rawky.collection.Frame
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.collection.Layer
import com.deflatedpickle.rawky.setting.RawkyDocument
import java.awt.Graphics2D

interface PaintLayer {
    companion object {
        val registry = Registry<String, PaintLayer>()
    }

    val name: String
    val layer: LayerCategory

    fun paint(doc: RawkyDocument?, frame: Int, layer: Int, g2d: Graphics2D)
}
