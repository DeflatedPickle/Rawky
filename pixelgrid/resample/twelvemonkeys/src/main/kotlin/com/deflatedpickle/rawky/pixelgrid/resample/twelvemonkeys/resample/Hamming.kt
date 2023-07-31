package com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample

import com.deflatedpickle.rawky.api.ResampleCollection
import com.twelvemonkeys.image.ResampleOp
import java.awt.image.BufferedImage

object Hamming : ResampleCollection.Resampler() {
    override val name = "Hamming"

    override fun resample(
        width: Int,
        height: Int,
        source: BufferedImage
    ): BufferedImage = ResampleOp(width, height, ResampleOp.FILTER_HAMMING)
        .filter(source, null)
}