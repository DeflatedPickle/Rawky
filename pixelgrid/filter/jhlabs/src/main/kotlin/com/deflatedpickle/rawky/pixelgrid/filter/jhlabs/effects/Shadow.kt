@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ShadowFilter
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Shadow : FilterCollection.ArgumentFilter<Shadow.ShadowPacket>() {
    override val name = "Shadow"
    override val category = "Effects"
    override val comment = "Create drop shadows"

    data class ShadowPacket(
        var radius: Float = 5f,
        var angle: Float = Math.PI.toFloat() * 6 / 4,
        var distance: Float = 5f,
        var opacity: Float = 0.5f,
        var addMargins: Boolean = false,
        var shadowOnly: Boolean = false,
        var shadowColour: Color = Color.BLACK,
    ) : Packet

    override val packetClass = ShadowPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = ShadowFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = ShadowFilter().apply {
        if (packet !is ShadowPacket) return@apply
        radius = packet.radius
        angle = packet.angle
        distance = packet.distance
        opacity = packet.opacity
        addMargins = packet.addMargins
        shadowOnly = packet.shadowOnly
        shadowColor = packet.shadowColour.rgb
    }.filter(source, null)
}