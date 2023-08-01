/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.UnsharpFilter
import java.awt.image.BufferedImage

object Unsharpen : FilterCollection.ArgumentFilter<Unsharpen.UnsharpenPacket>() {
    override val name = "Unsharpen"
    override val category = "Blur"
    override val comment = "Sharpening by unsharp masking"

    data class UnsharpenPacket(
        var amount: Float = 0.5f,
        var threshold: Int = 1,
    ) : Packet

    override val packetClass = UnsharpenPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = UnsharpFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = UnsharpFilter().apply {
        if (packet !is UnsharpenPacket) return@apply
        amount = packet.amount
        threshold = packet.threshold
    }.filter(source, null)
}
