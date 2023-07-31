@file:Suppress("unused")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.DisplaceFilter
import com.jhlabs.image.DissolveFilter
import com.jhlabs.image.FieldWarpFilter
import com.jhlabs.image.KaleidoscopeFilter
import com.jhlabs.image.MarbleFilter
import com.jhlabs.image.MirrorFilter
import com.jhlabs.image.PerspectiveFilter
import com.jhlabs.image.PinchFilter
import com.jhlabs.image.PolarFilter
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Polar : FilterCollection.ArgumentFilter<Polar.PolarPacket>() {
    override val name = "Pinch"
    override val category = "Distort"
    override val comment = "Convert to and from polar coordinates"

    enum class PolarConversion {
        RECTANGLE_TO_POLAR,
        POLAR_TO_RECTANGLE,
        INVERT_IN_CIRCLE,
    }

    data class PolarPacket(
        var type: PolarConversion = PolarConversion.RECTANGLE_TO_POLAR,
    ) : Packet

    override val packetClass = PolarPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = PolarFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = PolarFilter().apply {
        if (packet !is PolarPacket) return@apply
        type = packet.type.ordinal
    }.filter(source, null)
}