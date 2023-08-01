/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection", "unused")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture

import com.deflatedpickle.marvin.extensions.set
import com.deflatedpickle.rawky.api.FilterCollection
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.api.PaintMode
import com.jhlabs.image.GradientFilter
import java.awt.Color
import java.awt.Point
import java.awt.image.BufferedImage

object Gradient : FilterCollection.ArgumentFilter<Gradient.GradientPacket>() {
    override val name = "Gradient"
    override val category = "Texture"
    override val comment = "Draw radial, linear, fan and square gradients"

    enum class Type {
        LINEAR,
        BILINEAR,
        RADIAL,
        CONICAL,
        BICONICAL,
        SQUARE,
    }

    enum class Interpolation {
        LINEAR,
        CIRCLE_UP,
        CIRCLE_DOWN,
        SMOOTH,
    }

    data class GradientPacket(
        var firstPoint: Point = Point(0, 0),
        var lastPoint: Point = Point(64, 64),
        var firstColour: Color = Color.WHITE,
        var lastColour: Color = Color.BLACK,
        var repeat: Boolean = false,
        var type: Type = Type.LINEAR,
        var interpolation: Interpolation = Interpolation.LINEAR,
        val paintMode: PaintMode = PaintMode.NORMAL,
    ) : Packet

    override val packetClass = GradientPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = GradientFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = GradientFilter().apply {
        if (packet !is GradientPacket) return@apply
        point1 = packet.firstPoint
        point2 = packet.lastPoint
        set("color1", packet.firstColour.rgb)
        set("color2", packet.lastColour.rgb)
        set("repeat", packet.repeat)
        type = packet.type.ordinal
        interpolation = packet.interpolation.ordinal
        paintMode = packet.paintMode.ordinal
    }.filter(source, null)
}
