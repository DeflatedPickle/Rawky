/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.LookupFilter
import java.awt.image.BufferedImage

object Lookup : FilterCollection.Filter() {
    override val name = "Lookup"
    override val category = "Colour"
    override val comment = "Change image colors with a lookup table"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = LookupFilter().filter(source, null)
}
