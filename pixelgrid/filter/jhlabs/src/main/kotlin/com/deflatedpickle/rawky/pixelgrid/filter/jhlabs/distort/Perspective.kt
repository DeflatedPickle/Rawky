/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.PerspectiveFilter
import java.awt.image.BufferedImage

object Perspective : FilterCollection.Filter() {
    override val name = "Perspective"
    override val category = "Distort"
    override val comment = "Perspective distortion"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = PerspectiveFilter().filter(source, null)
}
