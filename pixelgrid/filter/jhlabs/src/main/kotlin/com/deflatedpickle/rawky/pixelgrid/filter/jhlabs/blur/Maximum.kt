/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.MaximumFilter
import java.awt.image.BufferedImage

object Maximum : FilterCollection.Filter() {
    override val name = "Maximum"
    override val category = "Blur"
    override val comment = "Dilation"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = MaximumFilter().filter(source, null)
}
