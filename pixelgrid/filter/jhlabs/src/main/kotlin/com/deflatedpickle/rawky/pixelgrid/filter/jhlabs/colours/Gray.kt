/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.GrayFilter
import java.awt.image.BufferedImage

object Gray : FilterCollection.Filter() {
    override val name = "Gray"
    override val category = "Colour"
    override val comment = "Grays out an image"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = GrayFilter().filter(source, null)
}
