package com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample

import com.deflatedpickle.rawky.api.ResampleCollection
import com.twelvemonkeys.image.ResampleOp
import java.awt.image.BufferedImage

object Cubic : ResampleCollection.Resampler() {
    override val name = "Cubic"

    override fun resample(
        width: Int,
        height: Int,
        source: BufferedImage
    ): BufferedImage = ResampleOp(width, height, ResampleOp.FILTER_CUBIC)
        .filter(source, null)
}