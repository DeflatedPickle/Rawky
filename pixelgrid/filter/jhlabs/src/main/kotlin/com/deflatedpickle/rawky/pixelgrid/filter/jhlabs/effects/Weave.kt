@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ChromeFilter
import com.jhlabs.image.ColorHalftoneFilter
import com.jhlabs.image.CrystallizeFilter
import com.jhlabs.image.PointillizeFilter
import com.jhlabs.image.ShadowFilter
import com.jhlabs.image.ShapeFilter
import com.jhlabs.image.StampFilter
import com.jhlabs.image.WeaveFilter
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Weave : FilterCollection.ArgumentFilter<Weave.WeavePacket>() {
    override val name = "Weave"
    override val category = "Effects"
    override val comment = "A woven image effect"

    data class WeavePacket(
        var width: Point2D.Float = Point2D.Float(16f, 16f),
        var gap: Point2D.Float = Point2D.Float(6f,6f),
        // TODO: support the matrix argument
        var useImageColours: Boolean = true,
        var roundThreads: Boolean = false,
        var shadeCrossings: Boolean = true
    ) : Packet

    override val packetClass = WeavePacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = WeaveFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = WeaveFilter().apply {
        if (packet !is WeavePacket) return@apply
        xWidth = packet.width.x
        yWidth = packet.width.y
        useImageColors = packet.useImageColours
        roundThreads = packet.roundThreads
        shadeCrossings = packet.shadeCrossings
    }.filter(source, null)
}