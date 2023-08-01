/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.DitherFilter
import java.awt.image.BufferedImage

object Dither : FilterCollection.ArgumentFilter<Dither.DitherPacket>() {
    override val name = "Dither"
    override val category = "Colour"
    override val comment = "Ordered dithering"

    enum class DitherMatrix(val matrix: IntArray) {
        MAGIC2X2(
            intArrayOf(
                0,
                2,
                3,
                1,
            ),
        ),
        MAGIC4X4(
            intArrayOf(
                14, 3, 13,
                11, 5, 8, 6,
                12, 2, 15, 1,
                7, 9, 4, 10,
            ),
        ),
        ORDERED4X4(DitherFilter.ditherOrdered4x4Matrix),
        LINE4X4(DitherFilter.ditherLines4x4Matrix),
        HALFTONE6X6(DitherFilter.dither90Halftone6x6Matrix),
        ORDERED6X6(DitherFilter.ditherOrdered6x6Matrix),
        ORDERED8X8(DitherFilter.ditherOrdered8x8Matrix),
        CLUSTER3(DitherFilter.ditherCluster3Matrix),
        CLUSTER4(DitherFilter.ditherCluster4Matrix),
        CLUSTER8(DitherFilter.ditherCluster8Matrix),
    }

    data class DitherPacket(
        // TODO: add an enum combobox widget
        var matrix: DitherMatrix = DitherMatrix.MAGIC4X4,
        var levels: Int = 6,
        var colourDither: Boolean = true,
    ) : Packet

    override val packetClass = DitherPacket::class

    override fun filter(
        source: BufferedImage,
    ): BufferedImage = DitherFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage,
    ): BufferedImage = DitherFilter().apply {
        if (packet !is DitherPacket) return@apply
        matrix = packet.matrix.matrix
        levels = packet.levels
        colorDither = packet.colourDither
    }.filter(source, null)
}
