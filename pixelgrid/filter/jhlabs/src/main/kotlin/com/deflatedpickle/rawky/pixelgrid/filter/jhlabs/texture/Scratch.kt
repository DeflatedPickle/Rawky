/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ScratchFilter
import java.awt.Color
import java.awt.image.BufferedImage

object Scratch : FilterCollection.ArgumentFilter<Scratch.ScratchPacket>() {
    override val name = "Scratch"
    override val category = "Texture"
    override val comment = "Render lines or scratches"

    data class ScratchPacket(
        var density: Float = 0.5f,
        var angle: Float = 0f,
        var angleVariation: Float = 0f,
        var width: Float = 0.5f,
        var length: Float = 0.5f,
        var colour: Color = Color.BLACK,
        var seed: Int = 0,
    ) : Packet

    override val packetClass = ScratchPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = ScratchFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = ScratchFilter().apply {
        if (packet !is ScratchPacket) return@apply
        density = packet.density
        angle = packet.angle
        angleVariation = packet.angleVariation
        width = packet.width
        length = packet.length
        color = packet.colour.rgb
        seed = packet.seed
    }.filter(source, null)
}
