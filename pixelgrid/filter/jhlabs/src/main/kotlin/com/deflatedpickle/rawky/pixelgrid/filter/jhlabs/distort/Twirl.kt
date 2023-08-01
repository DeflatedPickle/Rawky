/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.TwirlFilter
import java.awt.geom.Point2D
import java.awt.image.BufferedImage

object Twirl : FilterCollection.ArgumentFilter<Twirl.TwirlPacket>() {
    override val name = "Twirl"
    override val category = "Distort"
    override val comment = "Distort an image by twisting"

    data class TwirlPacket(
        var angle: Float = 0f,
        var center: Point2D.Float = Point2D.Float(0.5f, 0.5f),
        var radius: Float = 100f,
    ) : Packet

    override val packetClass = TwirlPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = TwirlFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = TwirlFilter().apply {
        if (packet !is TwirlPacket) return@apply
        angle = packet.angle
        centre = packet.center
        radius = packet.radius
    }.filter(source, null)
}
