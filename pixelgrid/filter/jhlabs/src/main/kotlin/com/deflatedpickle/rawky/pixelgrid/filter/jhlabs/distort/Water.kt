package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.DisplaceFilter
import com.jhlabs.image.DissolveFilter
import com.jhlabs.image.FieldWarpFilter
import com.jhlabs.image.KaleidoscopeFilter
import com.jhlabs.image.MarbleFilter
import com.jhlabs.image.MirrorFilter
import com.jhlabs.image.PerspectiveFilter
import com.jhlabs.image.PinchFilter
import com.jhlabs.image.PolarFilter
import com.jhlabs.image.RippleFilter
import com.jhlabs.image.ShearFilter
import com.jhlabs.image.SphereFilter
import com.jhlabs.image.SwimFilter
import com.jhlabs.image.TwirlFilter
import com.jhlabs.image.WarpFilter
import com.jhlabs.image.WaterFilter
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Water : FilterCollection.ArgumentFilter<Water.WaterPacket>() {
    override val name = "Water"
    override val category = "Distort"
    override val comment = "Simulate water ripples"

    data class WaterPacket(
        var wavelength: Float = 16f,
        var amplitude: Float = 10f,
        var centre: Point2D.Float = Point2D.Float(0.5f, 0.5f),
        var radius: Float = 50f,
    ) : Packet

    override val packetClass = WaterPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = WaterFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = WaterFilter().apply {
        if (packet !is WaterPacket) return@apply
        wavelength = packet.wavelength
        amplitude = packet.amplitude
        centre = packet.centre
        radius = packet.radius
    }.filter(source, null)
}