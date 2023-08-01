/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.TritoneFilter
import java.awt.Color
import java.awt.image.BufferedImage

object Tritone : FilterCollection.ArgumentFilter<Tritone.TritonePacket>() {
    override val name = "Tritone"
    override val category = "Colour"
    override val comment = "Create a tri-tone image"

    data class TritonePacket(
        var shadow: Color,
        var mid: Color,
        var high: Color,
    ) : Packet

    override val packetClass = TritonePacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = TritoneFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = TritoneFilter().apply {
        if (packet !is TritonePacket) return@apply
        shadowColor = packet.shadow.rgb
        midColor = packet.mid.rgb
        highColor = packet.high.rgb
    }.filter(source, null)
}
