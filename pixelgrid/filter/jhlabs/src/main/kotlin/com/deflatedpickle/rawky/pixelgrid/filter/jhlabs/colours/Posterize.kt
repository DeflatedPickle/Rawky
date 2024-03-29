/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.PosterizeFilter
import java.awt.image.BufferedImage

object Posterize : FilterCollection.ArgumentFilter<Posterize.PosterizePacket>() {
    override val name = "Posterize"
    override val category = "Colour"
    override val comment = "Posterization"

    data class PosterizePacket(
        var levels: Int = 6,
    ) : Packet

    override val packetClass = PosterizePacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = PosterizeFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = PosterizeFilter().apply {
        if (packet !is PosterizePacket) return@apply
        numLevels = packet.levels
    }.filter(source, null)
}
