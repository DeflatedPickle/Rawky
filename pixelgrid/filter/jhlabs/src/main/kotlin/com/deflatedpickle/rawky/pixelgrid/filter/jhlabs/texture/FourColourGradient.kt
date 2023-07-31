@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.BrushedMetalFilter
import com.jhlabs.image.CausticsFilter
import com.jhlabs.image.CellularFilter
import com.jhlabs.image.CheckFilter
import com.jhlabs.image.FBMFilter
import com.jhlabs.image.FlareFilter
import com.jhlabs.image.FourColorFilter
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object FourColourGradient : FilterCollection.ArgumentFilter<FourColourGradient.FourColourGradientPacket>() {
    override val name = "Four Colour Gradient"
    override val category = "Texture"
    override val comment = "Draw a four-color gradient"

    data class FourColourGradientPacket(
        var northWestColour: Color = Color.decode("0xffff0000"),
        var northEastColour: Color = Color.decode("0xffff00ff"),
        var southWestColour: Color = Color.decode("0xff0000ff"),
        var southEastColour: Color = Color.decode("0xff00ffff"),
    ) : Packet

    override val packetClass = FourColourGradientPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = FourColorFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = FourColorFilter().apply {
        if (packet !is FourColourGradientPacket) return@apply
        colorNW = packet.northWestColour.rgb
        colorNE = packet.northEastColour.rgb
        colorSW = packet.southWestColour.rgb
        colorSE = packet.southEastColour.rgb
    }.filter(source, null)
}