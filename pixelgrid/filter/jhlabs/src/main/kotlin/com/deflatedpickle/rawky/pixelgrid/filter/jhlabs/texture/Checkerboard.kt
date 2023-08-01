/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.CheckFilter
import java.awt.Color
import java.awt.Point
import java.awt.image.BufferedImage

object Checkerboard : FilterCollection.ArgumentFilter<Checkerboard.CheckerboardPacket>() {
    override val name = "Checkerboard"
    override val category = "Texture"
    override val comment = "Draw a checkerboard pattern"

    data class CheckerboardPacket(
        var scale: Point = Point(8, 8),
        var foreground: Color = Color.WHITE,
        var background: Color = Color.BLACK,
        var fuzziness: Int = 0,
        var angle: Float = 0f,
    ) : Packet

    override val packetClass = CheckerboardPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = CheckFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = CheckFilter().apply {
        if (packet !is CheckerboardPacket) return@apply
        xScale = packet.scale.x
        yScale = packet.scale.y
        foreground = packet.foreground.rgb
        background = packet.background.rgb
        fuzziness = packet.fuzziness
        angle = packet.angle
    }.filter(source, null)
}
