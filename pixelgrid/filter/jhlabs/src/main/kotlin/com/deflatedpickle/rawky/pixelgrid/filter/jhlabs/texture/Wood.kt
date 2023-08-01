/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.WoodFilter
import java.awt.image.BufferedImage

object Wood : FilterCollection.ArgumentFilter<Wood.WoodPacket>() {
    override val name = "Wood"
    override val category = "Texture"
    override val comment = "Create a wood texture"

    data class WoodPacket(
        var scale: Float = 200f,
        var stretch: Float = 10f,
        var angle: Float = Math.PI.toFloat() / 2,
        var rings: Float = 0.5f,
        var turbulence: Float = 0f,
        var fibres: Float = 0.5f,
        var gain: Float = 0.8f,
    ) : Packet

    override val packetClass = WoodPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = WoodFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = WoodFilter().apply {
        if (packet !is WoodPacket) return@apply
        scale = packet.scale
        stretch = packet.stretch
        angle = packet.angle
        rings = packet.rings
        turbulence = packet.turbulence
        fibres = packet.fibres
        gain = packet.gain
    }.filter(source, null)
}
