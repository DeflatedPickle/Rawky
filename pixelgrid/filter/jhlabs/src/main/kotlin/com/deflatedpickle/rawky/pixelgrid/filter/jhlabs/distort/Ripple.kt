/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.RippleFilter
import java.awt.geom.Point2D
import java.awt.image.BufferedImage

object Ripple : FilterCollection.ArgumentFilter<Ripple.RipplePacket>() {
    override val name = "Ripple"
    override val category = "Distort"
    override val comment = "Ripple distortion"

    enum class WaveType {
        SINE,
        SAWTOOTH,
        TRIANGLE,
        NOISE,
    }

    data class RipplePacket(
        var amplitude: Point2D.Float = Point2D.Float(5f, 0f),
        var wavelength: Point2D.Float = Point2D.Float(16f, 16f),
        var type: WaveType = WaveType.SINE,
    ) : Packet

    override val packetClass = RipplePacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = RippleFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = RippleFilter().apply {
        if (packet !is RipplePacket) return@apply
        xAmplitude = packet.amplitude.x
        yAmplitude = packet.amplitude.y
        xWavelength = packet.wavelength.x
        yWavelength = packet.wavelength.y
        waveType = packet.type.ordinal
    }.filter(source, null)
}
