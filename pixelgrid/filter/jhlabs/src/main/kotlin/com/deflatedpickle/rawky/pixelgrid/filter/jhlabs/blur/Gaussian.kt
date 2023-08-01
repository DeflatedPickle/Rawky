/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.GaussianFilter
import java.awt.image.BufferedImage

object Gaussian : FilterCollection.ArgumentFilter<Gaussian.GaussianPacket>() {
    override val name = "Gaussian"
    override val category = "Blur"
    override val comment = "Gaussian blur"

    data class GaussianPacket(
        var radius: Float = 1f,
    ) : Packet

    override val packetClass = GaussianPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = GaussianFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = GaussianFilter().apply {
        if (packet !is GaussianPacket) return@apply
        radius = packet.radius
    }.filter(source, null)
}
