@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture

import com.deflatedpickle.marvin.Colour
import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.BrushedMetalFilter
import com.jhlabs.image.CausticsFilter
import com.jhlabs.image.CellularFilter
import com.jhlabs.image.CheckFilter
import com.jhlabs.image.FBMFilter
import com.jhlabs.image.FlareFilter
import java.awt.Color
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object LensFlare : FilterCollection.ArgumentFilter<LensFlare.LensFlarePacket>() {
    override val name = "Lens Flare"
    override val category = "Texture"
    override val comment = "Create lens flares"

    data class LensFlarePacket(
        var radius: Float = 50f,
        var baseAmount: Float = 1f,
        var ringAmount: Float = 0.2f,
        var rayAmount: Float = 0.1f,
        var colour: Color = Color.WHITE,
        var centre: Point2D.Float = Point2D.Float(0.5f, 0.5f),
        var ringWidth: Float = 1.6f,
    ) : Packet

    override val packetClass = LensFlarePacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = FlareFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = FlareFilter().apply {
        if (packet !is LensFlarePacket) return@apply
        radius = packet.radius
        baseAmount = packet.baseAmount
        ringAmount = packet.ringAmount
        rayAmount = packet.rayAmount
        color = packet.colour.rgb
        centre = packet.centre
        ringWidth = packet.ringWidth
    }.filter(source, null)
}