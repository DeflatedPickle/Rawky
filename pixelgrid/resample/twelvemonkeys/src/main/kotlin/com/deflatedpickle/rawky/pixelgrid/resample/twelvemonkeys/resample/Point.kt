/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample

import com.deflatedpickle.rawky.api.ResampleCollection
import com.twelvemonkeys.image.ResampleOp
import java.awt.image.BufferedImage

object Point : ResampleCollection.Resampler() {
    override val name = "Point"

    override fun resample(
        width: Int,
        height: Int,
        source: BufferedImage,
    ): BufferedImage = ResampleOp(width, height, ResampleOp.FILTER_POINT)
        .filter(source, null)
}
