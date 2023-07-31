package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ChromeFilter
import com.jhlabs.image.ColorHalftoneFilter
import com.jhlabs.image.CrystallizeFilter
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Crystallize : FilterCollection.ArgumentFilter<Crystallize.CrystallizePacket>() {
    override val name = "Crystallize"
    override val category = "Effects"
    override val comment = "Make an image look like stained glass"

    data class CrystallizePacket(
        var edgeThickness: Float = 0.4f,
        var fadeEdges: Boolean = false,
        var edgeColour: Color = Color.BLACK
    ) : Packet

    override val packetClass = CrystallizePacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = CrystallizeFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = CrystallizeFilter().apply {
        if (packet !is CrystallizePacket) return@apply
        edgeThickness = packet.edgeThickness
        fadeEdges = packet.fadeEdges
        edgeColor = packet.edgeColour.rgb
    }.filter(source, null)
}