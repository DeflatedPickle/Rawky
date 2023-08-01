/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.DissolveFilter
import java.awt.image.BufferedImage

object Dissolve : FilterCollection.Filter() {
    override val name = "Dissolve"
    override val category = "Distort"
    override val comment = "Dissolves an image by turning random pixels transparent"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = DissolveFilter().filter(source, null)
}
