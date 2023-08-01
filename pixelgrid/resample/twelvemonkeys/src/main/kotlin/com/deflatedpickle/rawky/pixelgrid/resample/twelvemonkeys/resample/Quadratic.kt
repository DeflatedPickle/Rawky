/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample

import com.deflatedpickle.rawky.api.ResampleCollection
import com.twelvemonkeys.image.ResampleOp
import java.awt.image.BufferedImage

object Quadratic : ResampleCollection.Resampler() {
    override val name = "Quadratic"

    override fun resample(
        width: Int,
        height: Int,
        source: BufferedImage,
    ): BufferedImage = ResampleOp(width, height, ResampleOp.FILTER_QUADRATIC)
        .filter(source, null)
}
