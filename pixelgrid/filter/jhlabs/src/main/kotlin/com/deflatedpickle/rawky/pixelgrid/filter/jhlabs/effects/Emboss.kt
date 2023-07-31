package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.EmbossFilter
import com.jhlabs.image.ImageMath
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Emboss : FilterCollection.ArgumentFilter<Emboss.EmbossPacket>() {
    override val name = "Emboss"
    override val category = "Effects"
    override val comment = "Simple embossing"

    data class EmbossPacket(
        var azimuth: Float = 135.0f * ImageMath.PI / 180.0f,
        var elevation: Float = 30.0f * ImageMath.PI / 180f,
        var emboss: Boolean = false,
        var bumpHeight: Float = 0f,
    ) : Packet

    override val packetClass = EmbossPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = EmbossFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = EmbossFilter().apply {
        if (packet !is EmbossPacket) return@apply
        azimuth = packet.azimuth
        elevation = packet.elevation
        emboss = packet.emboss
        bumpHeight = packet.bumpHeight
    }.filter(source, null)
}