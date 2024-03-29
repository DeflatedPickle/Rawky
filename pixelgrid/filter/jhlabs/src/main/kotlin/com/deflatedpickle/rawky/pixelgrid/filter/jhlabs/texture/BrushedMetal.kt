/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.BrushedMetalFilter
import java.awt.Color
import java.awt.image.BufferedImage

object BrushedMetal : FilterCollection.ArgumentFilter<BrushedMetal.BrushedMetalPacket>() {
    override val name = "Brushed Metal"
    override val category = "Texture"
    override val comment = "Created brushed metal"

    data class BrushedMetalPacket(
        var radius: Int = 10,
        var amount: Float = 0.1f,
        var colour: Color = Color.decode("0xff888888"),
        var shine: Float = 0.1f,
        var monochrome: Boolean = true,
    ) : Packet

    override val packetClass = BrushedMetalPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = BrushedMetalFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = BrushedMetalFilter().apply {
        if (packet !is BrushedMetalPacket) return@apply
        radius = packet.radius
        amount = packet.amount
        color = packet.colour.rgb
        shine = packet.shine
        monochrome = packet.monochrome
    }.filter(source, null)
}
