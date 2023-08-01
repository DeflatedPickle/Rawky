/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.DisplaceFilter
import java.awt.image.BufferedImage

object Displace : FilterCollection.Filter() {
    override val name = "Displace"
    override val category = "Distort"
    override val comment = "A glass distortion effect"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = DisplaceFilter().filter(source, null)
}
