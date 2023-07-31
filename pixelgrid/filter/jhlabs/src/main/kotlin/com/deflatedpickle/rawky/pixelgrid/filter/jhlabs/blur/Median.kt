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
import java.awt.image.BufferedImage

object Median : FilterCollection.Filter() {
    override val name = "Median"
    override val category = "Blur"
    override val comment = "Median filter for noise reduction"

    override fun filter(
        source: BufferedImage
    ): BufferedImage = MedianFilter().filter(source, null)
}