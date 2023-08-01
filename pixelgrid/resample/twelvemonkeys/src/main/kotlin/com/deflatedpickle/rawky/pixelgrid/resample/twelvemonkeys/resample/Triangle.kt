/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample

import com.deflatedpickle.rawky.api.ResampleCollection
import com.twelvemonkeys.image.ResampleOp
import java.awt.image.BufferedImage

object Triangle : ResampleCollection.Resampler() {
    override val name = "Triangle"

    override fun resample(
        width: Int,
        height: Int,
        source: BufferedImage,
    ): BufferedImage = ResampleOp(width, height, ResampleOp.FILTER_TRIANGLE)
        .filter(source, null)
}
