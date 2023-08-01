/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.GainFilter
import java.awt.image.BufferedImage

object Gain : FilterCollection.ArgumentFilter<Gain.GainPacket>() {
    override val name = "Gain"
    override val category = "Colour"
    override val comment = "Adjusts gain and bias"

    data class GainPacket(
        var gain: Float = 0.5f,
        var bias: Float = 0.5f,
    ) : Packet

    override val packetClass = GainPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = GainFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = GainFilter().apply {
        if (packet !is GainPacket) return@apply
        gain = packet.gain
        bias = packet.bias
    }.filter(source, null)
}
