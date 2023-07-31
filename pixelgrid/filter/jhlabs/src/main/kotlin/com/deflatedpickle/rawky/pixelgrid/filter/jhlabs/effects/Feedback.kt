package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.ChromeFilter
import com.jhlabs.image.ColorHalftoneFilter
import com.jhlabs.image.FeedbackFilter
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Feedback : FilterCollection.ArgumentFilter<Feedback.FeedbackPacket>() {
    override val name = "Feedback"
    override val category = "Effects"
    override val comment = "A video feedback effect"

    data class FeedbackPacket(
        var center: Point2D.Float = Point2D.Float(0.5f, 0.5f),
        var distance: Float = 0f,
        var angle: Float = 0f,
        var rotation: Float = 0f,
        var zoom: Float = 0f,
        var startAlpha: Float = 1f,
        var endAlpha: Float = 1f,
        var iterations: Int = 0,
    ) : Packet

    override val packetClass = FeedbackPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = FeedbackFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = FeedbackFilter().apply {
        if (packet !is FeedbackPacket) return@apply
        centre = packet.center
        distance = packet.distance
        angle = packet.angle
        rotation = packet.rotation
        zoom = packet.zoom
        startAlpha = packet.startAlpha
        endAlpha = packet.endAlpha
        iterations = packet.iterations
    }.filter(source, null)
}