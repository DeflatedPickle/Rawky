/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection", "unused")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture

import com.deflatedpickle.rawky.api.FilterCollection
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.api.PaintMode
import com.jhlabs.image.FBMFilter
import java.awt.image.BufferedImage

object FractalBrownianMotion : FilterCollection.ArgumentFilter<FractalBrownianMotion.FractalBrownianMotionPacket>() {
    override val name = "Fractal Brownian Motion"
    override val category = "Texture"
    override val comment = "Fractal Brownian motion texturing"

    enum class BasisType {
        NOISE,
        RIDGED,
        VLNOISE,
        SCNOISE,
        CELLULAR,
    }

    data class FractalBrownianMotionPacket(
        var scale: Float = 32f,
        var stretch: Float = 1f,
        var angle: Float = 0f,
        var amount: Float = 1f,
        var octaves: Float = 4f,
        var lacunarity: Float = 2f,
        var gain: Float = 0.5f,
        var bias: Float = 0.5f,
        var paintMode: PaintMode = PaintMode.REPLACE,
        // TODO: support colourmap argument
        var basisType: BasisType = BasisType.NOISE,
    ) : Packet

    override val packetClass = FractalBrownianMotionPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = FBMFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = FBMFilter().apply {
        if (packet !is FractalBrownianMotionPacket) return@apply
        scale = packet.scale
        stretch = packet.stretch
        angle = packet.angle
        amount = packet.amount
        octaves = packet.octaves
        lacunarity = packet.lacunarity
        gain = packet.gain
        bias = packet.bias
        operation = packet.paintMode.ordinal
        basisType = packet.basisType.ordinal
    }.filter(source, null)
}
