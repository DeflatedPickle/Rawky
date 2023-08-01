/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.BumpFilter
import java.awt.image.BufferedImage

object Bump : FilterCollection.Filter() {
    override val name = "Bump"
    override val category = "Blur"
    override val comment = "Edge embossing"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = BumpFilter().filter(source, null)
}
