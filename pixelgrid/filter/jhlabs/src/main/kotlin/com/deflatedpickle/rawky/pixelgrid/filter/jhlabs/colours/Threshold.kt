@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.MapColorsFilter
import com.jhlabs.image.MaskFilter
import com.jhlabs.image.PosterizeFilter
import com.jhlabs.image.QuantizeFilter
import com.jhlabs.image.RescaleFilter
import com.jhlabs.image.SolarizeFilter
import com.jhlabs.image.ThresholdFilter
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Threshold : FilterCollection.ArgumentFilter<Threshold.ThresholdPacket>() {
    override val name = "Threshold"
    override val category = "Colour"
    override val comment = "Thresholding"

    data class ThresholdPacket(
        var lowerBound: Int = 127,
        var upperBound: Int = 127,
        var white: Color = Color.WHITE,
        var black: Color = Color.BLACK,
    ) : Packet

    override val packetClass = ThresholdPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = ThresholdFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = ThresholdFilter().apply {
        if (packet !is ThresholdPacket) return@apply
        lowerThreshold = packet.lowerBound
        upperThreshold = packet.upperBound
        white = packet.white.rgb
        black = packet.black.rgb
    }.filter(source, null)
}