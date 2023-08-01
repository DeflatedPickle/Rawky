/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ChannelMixFilter
import java.awt.image.BufferedImage

object ChannelMix : FilterCollection.Filter() {
    override val name = "Channel Mix"
    override val category = "Colour"
    override val comment = "Mixes the RGB channels"

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = ChannelMixFilter().filter(source, null)
}
