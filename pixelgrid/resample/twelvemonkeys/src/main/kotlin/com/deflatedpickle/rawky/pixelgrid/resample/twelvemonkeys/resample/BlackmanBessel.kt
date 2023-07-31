package com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample

import com.deflatedpickle.rawky.api.ResampleCollection
import com.twelvemonkeys.image.ResampleOp
import java.awt.image.BufferedImage

object BlackmanBessel : ResampleCollection.Resampler() {
    override val name = "Blackman Bessel"

    override fun resample(
        width: Int,
        height: Int,
        source: BufferedImage
    ): BufferedImage = ResampleOp(width, height, ResampleOp.FILTER_BLACKMAN_BESSEL)
        .filter(source, null)
}