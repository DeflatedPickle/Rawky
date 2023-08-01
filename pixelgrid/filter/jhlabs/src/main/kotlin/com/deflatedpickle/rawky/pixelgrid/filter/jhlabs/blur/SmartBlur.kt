/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.SmartBlurFilter
import java.awt.image.BufferedImage

object SmartBlur : FilterCollection.Filter() {
    override val name = "Smart Blur"
    override val category = "Blur"
    override val comment = "A thresholded blur for ironing out wrinkles"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = SmartBlurFilter().filter(source, null)
}
