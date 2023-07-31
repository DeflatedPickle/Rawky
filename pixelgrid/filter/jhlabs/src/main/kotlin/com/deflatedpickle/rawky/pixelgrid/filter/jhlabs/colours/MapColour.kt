package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.MapColorsFilter
import java.awt.image.BufferedImage

object MapColour : FilterCollection.Filter() {
    override val name = "Map Colour"
    override val category = "Colour"
    override val comment = "Replace a color"

    override fun filter(
        source: BufferedImage
    ): BufferedImage = MapColorsFilter().filter(source, null)
}