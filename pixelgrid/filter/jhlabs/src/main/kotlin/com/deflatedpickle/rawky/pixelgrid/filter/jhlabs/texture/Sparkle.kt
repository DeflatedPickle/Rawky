/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.SparkleFilter
import java.awt.Color
import java.awt.image.BufferedImage

object Sparkle : FilterCollection.ArgumentFilter<Sparkle.SparklePacket>() {
    override val name = "Sparkle"
    override val category = "Texture"
    override val comment = "Render sparkles"

    data class SparklePacket(
        var rays: Int = 50,
        var radius: Int = 25,
        var amount: Int = 50,
        var colour: Color = Color.WHITE,
        var randomness: Int = 25,
    ) : Packet

    override val packetClass = SparklePacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = SparkleFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = SparkleFilter().apply {
        if (packet !is SparklePacket) return@apply
        rays = packet.rays
        radius = packet.radius
        amount = packet.amount
        color = packet.colour.rgb
        randomness = packet.randomness
    }.filter(source, null)
}
