/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ShearFilter
import java.awt.geom.Point2D
import java.awt.image.BufferedImage

object Shear : FilterCollection.ArgumentFilter<Shear.ShearPacket>() {
    override val name = "Shear"
    override val category = "Distort"
    override val comment = "Shear an image"

    data class ShearPacket(
        var angle: Point2D.Float = Point2D.Float(0f, 0f),
        var resize: Boolean = true,
    ) : Packet

    override val packetClass = ShearPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = ShearFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = ShearFilter().apply {
        if (packet !is ShearPacket) return@apply
        xAngle = packet.angle.x
        yAngle = packet.angle.y
        isResize = packet.resize
    }.filter(source, null)
}
