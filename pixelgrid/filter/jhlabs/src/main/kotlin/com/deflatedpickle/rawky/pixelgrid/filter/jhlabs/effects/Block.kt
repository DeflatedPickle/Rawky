package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.BlockFilter
import com.jhlabs.image.ChromeFilter
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Block : FilterCollection.ArgumentFilter<Block.BlockPacket>() {
    override val name = "Chrome"
    override val category = "Effects"
    override val comment = "Mosaic or pixelate an image"

    data class BlockPacket(
        var blockSize: Int = 2,
    ) : Packet

    override val packetClass = BlockPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = BlockFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = BlockFilter().apply {
        if (packet !is BlockPacket) return@apply
        blockSize = packet.blockSize
    }.filter(source, null)
}