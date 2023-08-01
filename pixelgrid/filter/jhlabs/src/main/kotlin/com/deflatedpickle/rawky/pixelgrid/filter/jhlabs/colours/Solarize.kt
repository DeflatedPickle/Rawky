/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.SolarizeFilter
import java.awt.image.BufferedImage

object Solarize : FilterCollection.Filter() {
    override val name = "Solarize"
    override val category = "Colour"
    override val comment = "Solarization"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = SolarizeFilter().filter(source, null)
}
