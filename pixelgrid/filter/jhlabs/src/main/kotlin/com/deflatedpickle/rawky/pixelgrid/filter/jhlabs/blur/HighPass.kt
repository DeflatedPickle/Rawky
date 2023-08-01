/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.HighPassFilter
import java.awt.image.BufferedImage

object HighPass : FilterCollection.Filter() {
    override val name = "High Pass"
    override val category = "Blur"
    override val comment = "Remove low spatial frequencies"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = HighPassFilter().filter(source, null)
}
