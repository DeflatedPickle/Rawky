@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.BlurFilter
import com.jhlabs.image.DespeckleFilter
import com.jhlabs.image.GaussianFilter
import com.jhlabs.image.GlowFilter
import com.jhlabs.image.HighPassFilter
import com.jhlabs.image.LensBlurFilter
import com.jhlabs.image.MaximumFilter
import com.jhlabs.image.MedianFilter
import com.jhlabs.image.MinimumFilter
import java.awt.image.BufferedImage

object Minimum : FilterCollection.Filter() {
    override val name = "Minimum"
    override val category = "Blur"
    override val comment = "Erosion"

    override fun filter(
        source: BufferedImage
    ): BufferedImage = MinimumFilter().filter(source, null)
}