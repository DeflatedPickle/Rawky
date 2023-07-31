@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.BlurFilter
import com.jhlabs.image.DespeckleFilter
import com.jhlabs.image.GaussianFilter
import com.jhlabs.image.GlowFilter
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Glow : FilterCollection.ArgumentFilter<Glow.GlowPacket>() {
    override val name = "Glow"
    override val category = "Blur"
    override val comment = "Add a glow to an image"

    data class GlowPacket(
        var amount: Float = 0.5f,
    ) : Packet

    override val packetClass = GlowPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = GlowFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = GlowFilter().apply {
        if (packet !is GlowPacket) return@apply
        amount = packet.amount
    }.filter(source, null)
}