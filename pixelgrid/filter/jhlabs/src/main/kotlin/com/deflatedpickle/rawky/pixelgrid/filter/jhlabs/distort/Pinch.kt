/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.PinchFilter
import java.awt.geom.Point2D
import java.awt.image.BufferedImage

object Pinch : FilterCollection.ArgumentFilter<Pinch.PinchPacket>() {
    override val name = "Pinch"
    override val category = "Distort"
    override val comment = "Whirl-and-pinch distortion"

    data class PinchPacket(
        var angle: Float = 0f,
        var center: Point2D.Float = Point2D.Float(0.5f, 0.5f),
        var radius: Float = 100f,
        var amount: Float = 0.5f,
    ) : Packet

    override val packetClass = PinchPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = PinchFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = PinchFilter().apply {
        if (packet !is PinchPacket) return@apply
        angle = packet.angle
        centre = packet.center
        radius = packet.radius
        amount = packet.amount
    }.filter(source, null)
}
