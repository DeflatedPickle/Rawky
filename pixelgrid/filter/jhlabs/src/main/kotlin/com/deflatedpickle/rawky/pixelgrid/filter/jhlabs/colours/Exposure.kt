/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ExposureFilter
import java.awt.image.BufferedImage

object Exposure : FilterCollection.ArgumentFilter<Exposure.ExposurePacket>() {
    override val name = "Exposure"
    override val category = "Colour"
    override val comment = "Change the exposure of an image"

    data class ExposurePacket(
        var exposure: Float = 1f,
    ) : Packet

    override val packetClass = ExposurePacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = ExposureFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = ExposureFilter().apply {
        if (packet !is ExposurePacket) return@apply
        exposure = packet.exposure
    }.filter(source, null)
}
