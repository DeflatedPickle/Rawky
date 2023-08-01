/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.WarpFilter
import java.awt.image.BufferedImage

object Warp : FilterCollection.Filter() {
    override val name = "Warp"
    override val category = "Distort"
    override val comment = "A general grid image warp"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = WarpFilter().filter(source, null)
}
