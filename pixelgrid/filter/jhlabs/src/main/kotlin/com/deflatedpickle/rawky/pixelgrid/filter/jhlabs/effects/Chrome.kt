package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ChromeFilter
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Chrome : FilterCollection.ArgumentFilter<Chrome.ChromePacket>() {
    override val name = "Chrome"
    override val category = "Effects"
    override val comment = "Simulate chrome"

    data class ChromePacket(
        var amount: Float = 0.5f,
        var exposure: Float = 0.5f,
    ) : Packet

    override val packetClass = ChromePacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = ChromeFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = ChromeFilter().apply {
        if (packet !is ChromePacket) return@apply
        amount = packet.amount
        exposure = packet.exposure
    }.filter(source, null)
}