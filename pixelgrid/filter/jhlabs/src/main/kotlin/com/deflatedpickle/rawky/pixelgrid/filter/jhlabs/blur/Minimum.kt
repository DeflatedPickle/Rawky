/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.MinimumFilter
import java.awt.image.BufferedImage

object Minimum : FilterCollection.Filter() {
    override val name = "Minimum"
    override val category = "Blur"
    override val comment = "Erosion"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = MinimumFilter().filter(source, null)
}
