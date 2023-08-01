/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.MotionBlurFilter
import java.awt.image.BufferedImage

object MotionBlur : FilterCollection.ArgumentFilter<MotionBlur.MotionBlurPacket>() {
    override val name = "Motion Blur"
    override val category = "Blur"
    override val comment = "Simulate motion blur"

    data class MotionBlurPacket(
        var angle: Float = 0.0f,
        var distance: Float = 1.0f,
        var zoom: Float = 0.0f,
        var rotation: Float = 0.0f,
        var wrapEdges: Boolean = false,
        var premultiplyAlpha: Boolean = true,
    ) : Packet

    override val packetClass = MotionBlurPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = MotionBlurFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = MotionBlurFilter().apply {
        if (packet !is MotionBlurPacket) return@apply
        angle = packet.angle
        distance = packet.distance
        zoom = packet.zoom
        rotation = packet.rotation
        wrapEdges = packet.wrapEdges
        premultiplyAlpha = packet.premultiplyAlpha
    }.filter(source, null)
}
