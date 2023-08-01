/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.GrayscaleFilter
import java.awt.image.BufferedImage

object Grayscale : FilterCollection.Filter() {
    override val name = "Grayscale"
    override val category = "Colour"
    override val comment = "Converts to grayscale"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = GrayscaleFilter().filter(source, null)
}
