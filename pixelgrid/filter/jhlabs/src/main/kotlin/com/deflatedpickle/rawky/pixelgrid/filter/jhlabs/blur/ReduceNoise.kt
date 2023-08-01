/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ReduceNoiseFilter
import java.awt.image.BufferedImage

object ReduceNoise : FilterCollection.Filter() {
    override val name = "Reduce Noise"
    override val category = "Blur"
    override val comment = "Remove noise from an image"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = ReduceNoiseFilter().filter(source, null)
}
