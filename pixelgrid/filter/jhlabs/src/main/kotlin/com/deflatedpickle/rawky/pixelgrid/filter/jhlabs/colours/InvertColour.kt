package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.GrayscaleFilter
import com.jhlabs.image.InvertAlphaFilter
import com.jhlabs.image.InvertFilter
import java.awt.image.BufferedImage

object InvertColour : FilterCollection.Filter() {
    override val name = "Invert Colours"
    override val category = "Colour"
    override val comment = "Inverts image colors"

    override fun filter(
        source: BufferedImage
    ): BufferedImage = InvertFilter().filter(source, null)
}