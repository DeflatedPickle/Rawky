/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.MirrorFilter
import java.awt.image.BufferedImage

object Mirror : FilterCollection.ArgumentFilter<Mirror.MirrorPacket>() {
    override val name = "Mirror"
    override val category = "Distort"
    override val comment = "Mirror an image"

    data class MirrorPacket(
        var opacity: Float = 1f,
        var center: Float = 0.5f,
        var distance: Float = 0f,
        var angle: Float = 0f,
        var rotation: Float = 0f,
        var gap: Float = 0f,
    ) : Packet

    override val packetClass = MirrorPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = MirrorFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = MirrorFilter().apply {
        if (packet !is MirrorPacket) return@apply
        opacity = packet.opacity
        centreY = packet.center
        distance = packet.distance
        angle = packet.angle
        rotation = packet.rotation
        gap = packet.gap
    }.filter(source, null)
}
