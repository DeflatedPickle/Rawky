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
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Swim : FilterCollection.ArgumentFilter<Swim.SwimPacket>() {
    override val name = "Swim"
    override val category = "Distort"
    override val comment = "An \"underwater\" distortion effect"

    data class SwimPacket(
        var scale: Float = 32f,
        var stretch: Float = 1f,
        var angle: Float = 0f,
        var amount: Float = 0f,
        var turbulence: Float = 1f
    ) : Packet

    override val packetClass = SwimPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = SwimFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = SwimFilter().apply {
        if (packet !is SwimPacket) return@apply
        scale = packet.scale
        stretch = packet.stretch
        angle = packet.angle
        amount = packet.amount
        turbulence = packet.turbulence
    }.filter(source, null)
}