@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.SmearFilter
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Smear : FilterCollection.ArgumentFilter<Smear.SmearPacket>() {
    override val name = "Smear"
    override val category = "Texture"
    override val comment = "Smears pixels into adjacent areas"

    enum class Shape {
        CROSSES,
        LINES,
        CIRCLES,
        SQUARES,
    }

    data class SmearPacket(
        var angle: Float = 0f,
        var density: Float = 0.5f,
        var scatter: Float = 0f,
        var distance: Int = 8,
        var shape: Shape = Shape.LINES,
        var mix: Float = 0.5f,
        var fadeout: Int = 0,
        var background: Boolean = false,
    ) : Packet

    override val packetClass = SmearPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = SmearFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = SmearFilter().apply {
        if (packet !is SmearPacket) return@apply
        angle = packet.angle
        density = packet.density
        scatter = packet.scatter
        distance = packet.distance
        shape = packet.shape.ordinal
        mix = packet.mix
        fadeout = packet.fadeout
        background = packet.background
    }.filter(source, null)
}