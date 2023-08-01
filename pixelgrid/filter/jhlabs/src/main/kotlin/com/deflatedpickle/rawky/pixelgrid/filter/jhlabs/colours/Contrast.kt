/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ContrastFilter
import java.awt.image.BufferedImage

object Contrast : FilterCollection.ArgumentFilter<Contrast.ContrastPacket>() {
    override val name = "Contrast"
    override val category = "Colour"
    override val comment = "Adjusts brightness and contrast"

    data class ContrastPacket(
        var brightness: Float = 1f,
        var contrast: Float = 1f,
    ) : Packet

    override val packetClass = ContrastPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = ContrastFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = ContrastFilter().apply {
        if (packet !is ContrastPacket) return@apply
        brightness = packet.brightness
        contrast = packet.contrast
    }.filter(source, null)
}
