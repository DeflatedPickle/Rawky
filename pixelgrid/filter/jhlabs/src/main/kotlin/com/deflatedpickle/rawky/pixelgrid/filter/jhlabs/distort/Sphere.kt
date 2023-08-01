/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.SphereFilter
import java.awt.geom.Point2D
import java.awt.image.BufferedImage

object Sphere : FilterCollection.ArgumentFilter<Sphere.SpherePacket>() {
    override val name = "Sphere"
    override val category = "Distort"
    override val comment = "Lens distortion"

    data class SpherePacket(
        var refraction: Float = 1.5f,
        var radius: Float = 100f,
        var centre: Point2D.Float = Point2D.Float(0.5f, 0.5f),
    ) : Packet

    override val packetClass = SpherePacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = SphereFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = SphereFilter().apply {
        if (packet !is SpherePacket) return@apply
        refractionIndex = packet.refraction
        radius = packet.radius
        centre = packet.centre
    }.filter(source, null)
}
