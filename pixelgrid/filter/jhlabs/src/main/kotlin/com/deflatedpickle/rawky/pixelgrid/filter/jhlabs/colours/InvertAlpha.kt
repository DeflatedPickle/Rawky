/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.InvertAlphaFilter
import java.awt.image.BufferedImage

object InvertAlpha : FilterCollection.Filter() {
    override val name = "Invert Alpha"
    override val category = "Colour"
    override val comment = "Inverts the alpha channel"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = InvertAlphaFilter().filter(source, null)
}
