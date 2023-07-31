@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.MotionBlurFilter
import com.jhlabs.image.OilFilter
import com.jhlabs.image.ReduceNoiseFilter
import com.jhlabs.image.SharpenFilter
import java.awt.image.BufferedImage

object Sharpen : FilterCollection.Filter() {
    override val name = "Sharpen"
    override val category = "Blur"
    override val comment = "Simple sharpening"

    override fun filter(
        source: BufferedImage
    ): BufferedImage = SharpenFilter().filter(source, null)
}