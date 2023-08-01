/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.DiffuseFilter
import java.awt.image.BufferedImage

object Diffusion : FilterCollection.ArgumentFilter<Diffusion.DiffusionPacket>() {
    override val name = "Diffusion"
    override val category = "Colour"
    override val comment = "Error-diffusion dithering"

    data class DiffusionPacket(
        var scale: Float = 4f,
    ) : Packet

    override val packetClass = DiffusionPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = DiffuseFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = DiffuseFilter().apply {
        if (packet !is DiffusionPacket) return@apply
        scale = packet.scale
    }.filter(source, null)
}
