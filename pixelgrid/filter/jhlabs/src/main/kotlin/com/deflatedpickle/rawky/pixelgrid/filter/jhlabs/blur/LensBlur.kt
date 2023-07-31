@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.LensBlurFilter
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object LensBlur : FilterCollection.ArgumentFilter<LensBlur.LensBlurPacket>() {
    override val name = "Lens Blur"
    override val category = "Blur"
    override val comment = "Simulate camera lens blur"

    data class LensBlurPacket(
        var radius: Float = 10f,
        var bloom: Float = 2f,
        var bloomThreshold: Float = 255f,
        var sides: Int = 5,
    ) : Packet

    override val packetClass = LensBlurPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = LensBlurFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = LensBlurFilter().apply {
        if (packet !is LensBlurPacket) return@apply
        radius = packet.radius
        bloom = packet.bloom
        bloomThreshold = packet.bloomThreshold
        sides = packet.sides

    }.filter(source, null)
}