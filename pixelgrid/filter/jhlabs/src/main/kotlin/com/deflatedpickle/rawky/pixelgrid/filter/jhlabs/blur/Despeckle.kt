/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.DespeckleFilter
import java.awt.image.BufferedImage

object Despeckle : FilterCollection.Filter() {
    override val name = "Despeckle"
    override val category = "Blur"
    override val comment = "De-speckle an image"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = DespeckleFilter().filter(source, null)
}
