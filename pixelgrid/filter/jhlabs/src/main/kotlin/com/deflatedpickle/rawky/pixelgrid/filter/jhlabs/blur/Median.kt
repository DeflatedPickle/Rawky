/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.MedianFilter
import java.awt.image.BufferedImage

object Median : FilterCollection.Filter() {
    override val name = "Median"
    override val category = "Blur"
    override val comment = "Median filter for noise reduction"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = MedianFilter().filter(source, null)
}
