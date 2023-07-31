package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.DiffuseFilter
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object Diffuse : FilterCollection.ArgumentFilter<Diffuse.DiffusePacket>() {
    override val name = "Diffuse"
    override val category = "Distort"
    override val comment = "Diffuse the pixels of an image"

    data class DiffusePacket(
        var scale: Float = 4f,
    ) : Packet

    override val packetClass = DiffusePacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = DiffuseFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = DiffuseFilter().apply {
        if (packet !is DiffusePacket) return@apply
        scale = packet.scale
    }.filter(source, null)
}