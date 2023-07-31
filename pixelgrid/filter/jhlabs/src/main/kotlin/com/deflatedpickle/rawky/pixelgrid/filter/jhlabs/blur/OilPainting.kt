@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.GaussianFilter
import com.jhlabs.image.MotionBlurFilter
import com.jhlabs.image.OilFilter
import java.awt.image.BufferedImage

object OilPainting : FilterCollection.ArgumentFilter<OilPainting.OilPacket>() {
    override val name = "Oil Painting"
    override val category = "Blur"
    override val comment = "Oil painting effect"

    data class OilPacket(
        var radius: Int = 3,
        var levels: Int = 256,
    ) : Packet

    override val packetClass = OilPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = OilFilter().filter(source, null)


    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = OilFilter().apply {
        if (packet !is OilPacket) return@apply
        range = packet.radius
        levels = packet.levels
    }.filter(source, null)
}