package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.CircleFilter
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Circle : FilterCollection.ArgumentFilter<Circle.CirclePacket>() {
    override val name = "Circle"
    override val category = "Distort"
    override val comment = "Wrap an image around a circle"

    data class CirclePacket(
        var radius: Float = 10f,
        var height: Float = 20f,
        var angle: Float = 0f,
        var spreadAngle: Float = Math.PI.toFloat(),
        // TODO: add a component for this
        var centre: Point2D.Float = Point2D.Float(0.5f, 0.5f)
    ) : Packet

    override val packetClass = CirclePacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = CircleFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = CircleFilter().apply {
        if (packet !is CirclePacket) return@apply
        radius = packet.radius
        height = packet.height
        angle = packet.angle
        spreadAngle = packet.spreadAngle
        centre = packet.centre
    }.filter(source, null)
}