/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection", "unused")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ShapeFilter
import java.awt.image.BufferedImage

object Shape : FilterCollection.ArgumentFilter<Shape.ShapePacket>() {
    override val name = "Shape"
    override val category = "Effects"
    override val comment = "Create bump maps for lighting"

    enum class ShapeType {
        LINEAR,
        CIRCLE_UP,
        CIRCLE_DOWN,
        SMOOTH,
    }

    data class ShapePacket(
        var factor: Float = 1f,
        // TODO: support colourmap argument
        var useAlpha: Boolean = true,
        var invert: Boolean = false,
        var merge: Boolean = false,
        var type: ShapeType = ShapeType.LINEAR,
    ) : Packet

    override val packetClass = ShapePacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = ShapeFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = ShapeFilter().apply {
        if (packet !is ShapePacket) return@apply
        factor = packet.factor
        useAlpha = packet.useAlpha
        invert = packet.invert
        merge = packet.merge
        type = packet.type.ordinal
    }.filter(source, null)
}
