package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.GrayscaleFilter
import com.jhlabs.image.HSBAdjustFilter
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object AdjustHSB : FilterCollection.ArgumentFilter<AdjustHSB.AdjustHSBPacket>() {
    override val name = "Adjust HSB"
    override val category = "Colour"
    override val comment = "Adjusts hue, saturation and brightness"

    data class AdjustHSBPacket(
        var hue: Float = 0f,
        var saturation: Float = 0f,
        var brightness: Float = 0f,
    ) : Packet

    override val packetClass = AdjustHSBPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = HSBAdjustFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = HSBAdjustFilter().apply {
        if (packet !is AdjustHSBPacket) return@apply
        hFactor = packet.hue
        sFactor = packet.saturation
        bFactor = packet.brightness
    }.filter(source, null)
}