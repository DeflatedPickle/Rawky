package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.BlockFilter
import com.jhlabs.image.BorderFilter
import com.jhlabs.image.ChromeFilter
import java.awt.image.BufferedImage

object Border : FilterCollection.Filter() {
    override val name = "Border"
    override val category = "Effects"
    override val comment = "Add a border"

    override fun filter(
        source: BufferedImage
    ): BufferedImage = BorderFilter().filter(source, null)
}