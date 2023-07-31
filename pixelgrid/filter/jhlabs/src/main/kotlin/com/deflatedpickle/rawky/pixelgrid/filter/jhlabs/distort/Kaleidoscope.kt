package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.DisplaceFilter
import com.jhlabs.image.DissolveFilter
import com.jhlabs.image.FieldWarpFilter
import com.jhlabs.image.KaleidoscopeFilter
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Kaleidoscope : FilterCollection.ArgumentFilter<Kaleidoscope.KaleidoscopePacket>() {
    override val name = "Kaleidoscope"
    override val category = "Distort"
    override val comment = "A kaleidoscope effect"

    data class KaleidoscopePacket(
        var angle: Float = 0f,
        var angle2: Float = 0f,
        var centre: Point2D.Float = Point2D.Float(0.5f, 0.5f),
        var sides: Int = 3,
    ) : Packet

    override val packetClass = KaleidoscopePacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = KaleidoscopeFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = KaleidoscopeFilter().apply {
        if (packet !is KaleidoscopePacket) return@apply
        angle = packet.angle
        angle2 = packet.angle2
        centre = packet.centre
        sides = packet.sides
    }.filter(source, null)
}