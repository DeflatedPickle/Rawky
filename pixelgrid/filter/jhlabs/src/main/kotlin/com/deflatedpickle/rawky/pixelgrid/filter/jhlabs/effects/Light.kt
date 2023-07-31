package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ColorHalftoneFilter
import com.jhlabs.image.LightFilter
import java.awt.image.BufferedImage

object Light : FilterCollection.Filter() {
    override val name = "Light"
    override val category = "Effects"
    override val comment = "Simulate lights on an bump-mapped image"

    override fun filter(
        source: BufferedImage
    ): BufferedImage = LightFilter().filter(source, null)
}