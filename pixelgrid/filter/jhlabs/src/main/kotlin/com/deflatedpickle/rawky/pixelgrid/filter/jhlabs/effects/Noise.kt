@file:Suppress("unused")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ColorHalftoneFilter
import com.jhlabs.image.LightFilter
import com.jhlabs.image.NoiseFilter
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Noise : FilterCollection.ArgumentFilter<Noise.NoisePacket>() {
    override val name = "Noise"
    override val category = "Effects"
    override val comment = "Add noise"

    enum class NoiseDistribution {
        GAUSSIAN,
        UNIFORM,
    }

    data class NoisePacket(
        var amount: Int = 25,
        var distribution: NoiseDistribution = NoiseDistribution.UNIFORM,
        var monochrome: Boolean = false,
        var density: Float = 1f,
    ) : Packet

    override val packetClass = NoisePacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = NoiseFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = NoiseFilter().apply {
        if (packet !is NoisePacket) return@apply
        amount = packet.amount
        distribution = packet.distribution.ordinal
        monochrome = packet.monochrome
        density = packet.density
    }.filter(source, null)
}