/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.RGBAdjustFilter
import java.awt.image.BufferedImage

object AdjustRGB : FilterCollection.ArgumentFilter<AdjustRGB.AdjustRGBPacket>() {
    override val name = "Adjust RGB"
    override val category = "Colour"
    override val comment = "Adjusts red, green and blue levels"

    data class AdjustRGBPacket(
        var red: Float = 0f,
        var green: Float = 0f,
        var blue: Float = 0f,
    ) : Packet

    override val packetClass = AdjustRGBPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = RGBAdjustFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = RGBAdjustFilter().apply {
        if (packet !is AdjustRGBPacket) return@apply
        rFactor = packet.red
        gFactor = packet.green
        bFactor = packet.blue
    }.filter(source, null)
}
