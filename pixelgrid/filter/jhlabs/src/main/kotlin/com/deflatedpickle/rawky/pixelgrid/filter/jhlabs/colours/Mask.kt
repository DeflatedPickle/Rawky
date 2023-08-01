/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.MaskFilter
import java.awt.image.BufferedImage

object Mask : FilterCollection.Filter() {
    override val name = "Mask"
    override val category = "Colour"
    override val comment = "Channel masking"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = MaskFilter().filter(source, null)
}
