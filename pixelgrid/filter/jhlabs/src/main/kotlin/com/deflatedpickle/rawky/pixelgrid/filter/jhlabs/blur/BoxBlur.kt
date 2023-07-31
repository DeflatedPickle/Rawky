@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.BlurFilter
import com.jhlabs.image.BoxBlurFilter
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object BoxBlur : FilterCollection.ArgumentFilter<BoxBlur.BoxBlurPacket>() {
    override val name = "Box Blur"
    override val category = "Blur"
    override val comment = "Box blur"

    data class BoxBlurPacket(
        var horizontalRadius: Float = 1f,
        var verticalRadius: Float = 1f,
        var iterations: Int = 1,
        var premultiplyAlpha: Boolean = true,
    ) : Packet

    override val packetClass = BoxBlurPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = BoxBlurFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = BoxBlurFilter().apply {
        if (packet !is BoxBlurPacket) return@apply
        hRadius = packet.horizontalRadius
        vRadius = packet.verticalRadius
        iterations = packet.iterations
        premultiplyAlpha = packet.premultiplyAlpha
    }.filter(source, null)
}