@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.BlurFilter
import java.awt.image.BufferedImage

object Blur : FilterCollection.Filter() {
    override val name = "3x3 Blur"
    override val category = "Blur"
    override val comment = "Simple blur"

    override fun filter(
        source: BufferedImage
    ): BufferedImage = BlurFilter().filter(source, null)
}