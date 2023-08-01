/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample

import com.deflatedpickle.rawky.api.ResampleCollection
import com.twelvemonkeys.image.ResampleOp
import java.awt.image.BufferedImage

object Hanning : ResampleCollection.Resampler() {
    override val name = "Hanning"

    override fun resample(
        width: Int,
        height: Int,
        source: BufferedImage,
    ): BufferedImage = ResampleOp(width, height, ResampleOp.FILTER_HANNING)
        .filter(source, null)
}
