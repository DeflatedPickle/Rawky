/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection", "unused")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.CellularFilter
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

object Cellular : FilterCollection.ArgumentFilter<Cellular.CellularPacket>() {
    override val name = "Cellular"
    override val category = "Texture"
    override val comment = "Cellular texturing"

    enum class GridType {
        RANDOM,
        SQUARE,
        HEXAGONAL,
        OCTAGONAL,
        TRIANGULAR,
    }

    data class CellularPacket(
        var scale: Float = 32f,
        var stretch: Float = 1f,
        var angle: Float = 0f,
        var coefficient: Rectangle2D.Float = Rectangle2D.Float(1f, 0f, 0f, 0f),
        var angleCoefficient: Float = 0f,
        var gradientCoefficient: Float = 0f,
        var amount: Float = 1f,
        var turbulence: Float = 1f,
        var gain: Float = 0.5f,
        var bias: Float = 0.5f,
        var distancePower: Float = 2f,
        // TODO: support colormap argument
        var randomness: Float = 0f,
        var gridType: GridType = GridType.HEXAGONAL,
    ) : Packet

    override val packetClass = CellularPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = CellularFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = CellularFilter().apply {
        if (packet !is CellularPacket) return@apply
        scale = packet.scale
        stretch = packet.stretch
        angle = packet.angle
        setCoefficient(0, packet.coefficient.x)
        setCoefficient(1, packet.coefficient.y)
        setCoefficient(2, packet.coefficient.width)
        setCoefficient(3, packet.coefficient.height)
        angleCoefficient = packet.angleCoefficient
        gradientCoefficient = packet.gradientCoefficient
        amount = packet.amount
        turbulence = packet.turbulence
        gain = packet.gain
        bias = packet.bias
        distancePower = packet.distancePower
        randomness = packet.randomness
        gridType = packet.gridType.ordinal
    }.filter(source, null)
}
