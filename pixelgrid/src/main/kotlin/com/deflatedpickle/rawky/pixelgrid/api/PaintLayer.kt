package com.deflatedpickle.rawky.pixelgrid.api

import com.deflatedpickle.marvin.registry.Registry
import java.awt.Graphics2D

interface PaintLayer {
    companion object {
        val registry = Registry<String, PaintLayer>()
    }

    val name: String
    val layer: Layer

    fun paint(g2d: Graphics2D)
}