/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.GammaFilter
import java.awt.image.BufferedImage

object Gamma : FilterCollection.ArgumentFilter<Gamma.GammaPacket>() {
    override val name = "Gamma"
    override val category = "Colour"
    override val comment = "Adjusts image gamma"

    data class GammaPacket(
        var red: Float = 1f,
        var green: Float = 1f,
        var blue: Float = 1f,
    ) : Packet

    override val packetClass = GammaPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = GammaFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = GammaFilter().apply {
        if (packet !is GammaPacket) return@apply
        setGamma(packet.red, packet.green, packet.blue)
    }.filter(source, null)
}
