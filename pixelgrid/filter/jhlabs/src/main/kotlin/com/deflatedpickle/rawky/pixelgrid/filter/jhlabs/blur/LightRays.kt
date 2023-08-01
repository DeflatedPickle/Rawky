/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.RaysFilter
import java.awt.image.BufferedImage

object LightRays : FilterCollection.ArgumentFilter<LightRays.LightRaysPacket>() {
    override val name = "Light Rays"
    override val category = "Blur"
    override val comment = "Create light rays"

    data class LightRaysPacket(
        var opacity: Float = 1.0f,
        var threshold: Float = 0.0f,
        var strength: Float = 0.5f,
        var raysOnly: Boolean = false,
    ) : Packet

    override val packetClass = LightRaysPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = RaysFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = RaysFilter().apply {
        if (packet !is LightRaysPacket) return@apply
        opacity = packet.opacity
        threshold = packet.threshold
        strength = packet.strength
        raysOnly = packet.raysOnly
    }.filter(source, null)
}
