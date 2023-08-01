/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.LevelsFilter
import java.awt.image.BufferedImage

object AdjustLevels : FilterCollection.ArgumentFilter<AdjustLevels.AdjustLevelsPacket>() {
    override val name = "Adjust Levels"
    override val category = "Colour"
    override val comment = "Adjust image levels"

    data class AdjustLevelsPacket(
        var lowLevel: Float = 0f,
        var highLevel: Float = 1f,
        var lowOutputLevel: Float = 0f,
        var highOutputLevel: Float = 1f,
    ) : Packet

    override val packetClass = AdjustLevelsPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = LevelsFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = LevelsFilter().apply {
        if (packet !is AdjustLevelsPacket) return@apply
        lowLevel = packet.lowLevel
        highLevel = packet.highLevel
        lowOutputLevel = packet.lowOutputLevel
        highOutputLevel = packet.highOutputLevel
    }.filter(source, null)
}
