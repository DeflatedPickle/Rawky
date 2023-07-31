package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ChannelMixFilter
import com.jhlabs.image.CurvesFilter
import java.awt.image.BufferedImage

object Curves : FilterCollection.Filter() {
    override val name = "Curves"
    override val category = "Colour"
    override val comment = "Apply adjustment curves to an image"

    override fun filter(
        source: BufferedImage
    ): BufferedImage = CurvesFilter().filter(source, null)
}