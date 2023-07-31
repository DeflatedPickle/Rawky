@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.BrushedMetalFilter
import com.jhlabs.image.CausticsFilter
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Caustics : FilterCollection.ArgumentFilter<Caustics.CausticsPacket>() {
    override val name = "Caustics"
    override val category = "Texture"
    override val comment = "Simulate underwater caustics"

    data class CausticsPacket(
        var scale: Float = 32f,
        var brightness: Int = 10,
        var amount: Float = 1f,
        var turbulence: Float = 1f,
        var dispersion: Float = 0f,
        var samples: Int = 2,
        var backgroundColour: Color = Color.decode("0xff799fff")
    ) : Packet

    override val packetClass = CausticsPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = CausticsFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = CausticsFilter().apply {
        if (packet !is CausticsPacket) return@apply
        scale = packet.scale
        brightness = packet.brightness
        amount = packet.amount
        turbulence = packet.turbulence
        dispersion = packet.dispersion
        samples = packet.samples
        bgColor = packet.backgroundColour.rgb
    }.filter(source, null)
}