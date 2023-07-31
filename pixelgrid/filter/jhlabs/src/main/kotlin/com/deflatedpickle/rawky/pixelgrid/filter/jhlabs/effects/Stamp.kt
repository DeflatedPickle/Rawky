@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ChromeFilter
import com.jhlabs.image.ColorHalftoneFilter
import com.jhlabs.image.CrystallizeFilter
import com.jhlabs.image.PointillizeFilter
import com.jhlabs.image.ShadowFilter
import com.jhlabs.image.ShapeFilter
import com.jhlabs.image.StampFilter
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Stamp : FilterCollection.ArgumentFilter<Stamp.StampPacket>() {
    override val name = "Stamp"
    override val category = "Effects"
    override val comment = "A rubber stamp effect"

    data class StampPacket(
        var threshold: Float = 0f,
        var softness: Float = 0.5f,
        var radius: Float = 5f,
        var white: Color = Color.WHITE,
        var black: Color = Color.BLACK,
    ) : Packet

    override val packetClass = StampPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = StampFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = StampFilter().apply {
        if (packet !is StampPacket) return@apply
        threshold = packet.threshold
        softness = packet.softness
        radius = packet.radius
        white = packet.white.rgb
        black = packet.black.rgb
    }.filter(source, null)
}