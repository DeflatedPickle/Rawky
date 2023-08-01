/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture

import com.deflatedpickle.rawky.api.FilterCollection
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.api.PaintMode
import com.jhlabs.image.TextureFilter
import java.awt.image.BufferedImage

object PerlinNoise : FilterCollection.ArgumentFilter<PerlinNoise.PerlinNoisePacket>() {
    override val name = "Perlin Noise"
    override val category = "Texture"
    override val comment = "Perlin noise texturing"

    data class PerlinNoisePacket(
        var scale: Float = 32f,
        var stretch: Float = 1f,
        var angle: Float = 0f,
        var turbulence: Float = 1f,
        var gain: Float = 0.5f,
        var bias: Float = 0.5f,
        var paintMode: PaintMode = PaintMode.REPLACE,
    ) : Packet

    override val packetClass = PerlinNoisePacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = TextureFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = TextureFilter().apply {
        if (packet !is PerlinNoisePacket) return@apply
        scale = packet.scale
        stretch = packet.stretch
        angle = packet.angle
        turbulence = packet.turbulence
        gain = packet.gain
        bias = packet.bias
        operation = packet.paintMode.ordinal
    }.filter(source, null)
}
