/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.CurlFilter
import java.awt.image.BufferedImage

object Curl : FilterCollection.ArgumentFilter<Curl.CurlPacket>() {
    override val name = "Curl"
    override val category = "Distort"
    override val comment = "A page curl effect"

    data class CurlPacket(
        var angle: Float = 0f,
        var transition: Float = 0f,
        var radius: Float = 0f,
    ) : Packet

    override val packetClass = CurlPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = CurlFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = CurlFilter().apply {
        if (packet !is CurlPacket) return@apply
        angle = packet.angle
        transition = packet.transition
        radius = packet.radius
    }.filter(source, null)
}
