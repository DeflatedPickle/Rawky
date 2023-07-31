@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.MotionBlurFilter
import com.jhlabs.image.OilFilter
import com.jhlabs.image.ReduceNoiseFilter
import com.jhlabs.image.SharpenFilter
import com.jhlabs.image.SmartBlurFilter
import com.jhlabs.image.UnsharpFilter
import com.jhlabs.image.VariableBlurFilter
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object VariableBlur : FilterCollection.ArgumentFilter<VariableBlur.VariableBlurPacket>() {
    override val name = "Variable Blur"
    override val category = "Blur"
    override val comment = "Blurring with a variable radius taken from a mask"

    data class VariableBlurPacket(
        var hRadius: Int = 1,
        var vRadius: Int = 1,
        var iterations: Int = 1,
        // TODO: add a component to deal with this
        var blurMask: BufferedImage? = null,
        var premultiplyAlpha: Boolean = true,
    ) : Packet

    override val packetClass = VariableBlurPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = VariableBlurFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = VariableBlurFilter().apply {
        if (packet !is VariableBlurPacket) return@apply
        hRadius = packet.hRadius
        vRadius = packet.vRadius
        iterations = packet.iterations
        blurMask = packet.blurMask
        premultiplyAlpha = packet.premultiplyAlpha
    }.filter(source, null)
}