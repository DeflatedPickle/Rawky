/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.RescaleFilter
import java.awt.image.BufferedImage

object Rescale : FilterCollection.ArgumentFilter<Rescale.RescalePacket>() {
    override val name = "Rescale"
    override val category = "Colour"
    override val comment = "Multiplies colors by a scaling factor"

    data class RescalePacket(
        var scale: Float = 1f,
    ) : Packet

    override val packetClass = RescalePacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = RescaleFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = RescaleFilter().apply {
        if (packet !is RescalePacket) return@apply
        scale = packet.scale
    }.filter(source, null)
}
