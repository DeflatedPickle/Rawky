package com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample

import com.deflatedpickle.rawky.api.ResampleCollection
import com.twelvemonkeys.image.ResampleOp
import java.awt.image.BufferedImage

object Catrom : ResampleCollection.Resampler() {
    override val name = "Catrom"

    override fun resample(
        width: Int,
        height: Int,
        source: BufferedImage
    ): BufferedImage = ResampleOp(width, height, ResampleOp.FILTER_CATROM)
        .filter(source, null)
}