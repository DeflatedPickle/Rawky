/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.MarbleFilter
import java.awt.geom.Point2D
import java.awt.image.BufferedImage

object Marble : FilterCollection.ArgumentFilter<Marble.MarblePacket>() {
    override val name = "Marble"
    override val category = "Distort"
    override val comment = "A marbling effect"

    data class MarblePacket(
        val scale: Point2D.Float = Point2D.Float(4f, 4f),
        var amount: Float = 1f,
        var turbulence: Float = 1f,
    ) : Packet

    override val packetClass = MarblePacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = MarbleFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = MarbleFilter().apply {
        if (packet !is MarblePacket) return@apply
        xScale = packet.scale.x
        yScale = packet.scale.y
        amount = packet.amount
        turbulence = packet.turbulence
    }.filter(source, null)
}
