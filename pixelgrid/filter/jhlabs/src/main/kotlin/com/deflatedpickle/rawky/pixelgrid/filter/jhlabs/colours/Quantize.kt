/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.QuantizeFilter
import java.awt.image.BufferedImage

object Quantize : FilterCollection.ArgumentFilter<Quantize.QuantizePacket>() {
    override val name = "Quantize"
    override val category = "Colour"
    override val comment = "Quantize an image to 256 colours"

    data class QuantizePacket(
        var dither: Boolean = false,
        var colours: Int = 256,
        var serpentine: Boolean = true,
    ) : Packet

    override val packetClass = QuantizePacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = QuantizeFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = QuantizeFilter().apply {
        if (packet !is QuantizePacket) return@apply
        dither = packet.dither
        numColors = packet.colours
        serpentine = packet.serpentine
    }.filter(source, null)
}
