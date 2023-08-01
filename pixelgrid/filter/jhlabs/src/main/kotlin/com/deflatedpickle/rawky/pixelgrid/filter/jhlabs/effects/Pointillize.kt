/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.PointillizeFilter
import java.awt.Color
import java.awt.image.BufferedImage

object Pointillize : FilterCollection.ArgumentFilter<Pointillize.PointillizePacket>() {
    override val name = "Pointillize"
    override val category = "Effects"
    override val comment = "Draw an image as colored spots"

    data class PointillizePacket(
        var edgeThickness: Float = 0.4f,
        var fadeEdges: Boolean = false,
        var edgeColour: Color = Color.BLACK,
        var fuzziness: Float = 0.1f,
    ) : Packet

    override val packetClass = PointillizePacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = PointillizeFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = PointillizeFilter().apply {
        if (packet !is PointillizePacket) return@apply
        edgeThickness = packet.edgeThickness
        fadeEdges = packet.fadeEdges
        edgeColor = packet.edgeColour.rgb
        fuzziness = packet.fuzziness
    }.filter(source, null)
}
