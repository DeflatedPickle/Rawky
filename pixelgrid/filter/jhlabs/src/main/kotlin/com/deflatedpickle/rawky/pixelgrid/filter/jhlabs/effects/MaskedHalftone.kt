package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ColorHalftoneFilter
import java.awt.image.BufferedImage

object MaskedHalftone : FilterCollection.Filter() {
    override val name = "Masked Halftone"
    override val category = "Effects"
    override val comment = "Performs a simple half-toning of an image using a mask"

    override fun filter(
        source: BufferedImage
    ): BufferedImage = ColorHalftoneFilter().filter(source, null)
}