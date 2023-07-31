@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.BrushedMetalFilter
import com.jhlabs.image.CausticsFilter
import com.jhlabs.image.CellularFilter
import com.jhlabs.image.CheckFilter
import com.jhlabs.image.FBMFilter
import com.jhlabs.image.FlareFilter
import com.jhlabs.image.PlasmaFilter
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Plasma : FilterCollection.ArgumentFilter<Plasma.PlasmaPacket>() {
    override val name = "Plasma"
    override val category = "Texture"
    override val comment = "Create plasma"

    data class PlasmaPacket(
        var turbulence: Float = 1f,
        var scaling: Float = 0f,
        // TODO: support colourmap argument
        var seed: Int = 567,
        var useImageColours: Boolean = false,
    ) : Packet

    override val packetClass = PlasmaPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = PlasmaFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = PlasmaFilter().apply {
        if (packet !is PlasmaPacket) return@apply
        turbulence = packet.turbulence
        scaling = packet.scaling
        seed = packet.seed
        useImageColors = packet.useImageColours
    }.filter(source, null)
}