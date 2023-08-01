/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ColorHalftoneFilter
import java.awt.image.BufferedImage

object DottedHalftone : FilterCollection.ArgumentFilter<DottedHalftone.DottedHalftonePacket>() {
    override val name = "Dotted Halftone"
    override val category = "Effects"
    override val comment = "Simulates the dot patterns of colour halftone printing"

    data class DottedHalftonePacket(
        var dotRadius: Float = 2f,
        var cyanScreenAngle: Float = Math.toRadians(108.0).toFloat(),
        var magentaScreenAngle: Float = Math.toRadians(162.0).toFloat(),
        var yellowScreenAngle: Float = Math.toRadians(90.0).toFloat(),
    ) : Packet

    override val packetClass = DottedHalftonePacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = ColorHalftoneFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = ColorHalftoneFilter().apply {
        if (packet !is DottedHalftonePacket) return@apply
        setdotRadius(packet.dotRadius)
        cyanScreenAngle = packet.cyanScreenAngle
        magentaScreenAngle = packet.magentaScreenAngle
        yellowScreenAngle = packet.yellowScreenAngle
    }.filter(source, null)
}
