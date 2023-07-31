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
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

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
        source: BufferedImage
    ): BufferedImage = TwirlFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = TwirlFilter().apply {
        if (packet !is TwirlPacket) return@apply
        angle = packet.angle
        centre = packet.center
        radius = packet.radius
    }.filter(source, null)
}