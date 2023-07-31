package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.DisplaceFilter
import com.jhlabs.image.DissolveFilter
import com.jhlabs.image.FieldWarpFilter
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

object FieldWarp : FilterCollection.ArgumentFilter<FieldWarp.FieldWarpPacket>() {
    override val name = "Field Warp"
    override val category = "Distort"
    override val comment = "Warp images using a field warp algorithm"

    data class FieldWarpPacket(
        var amount: Float = 1f,
        var power: Float = 1f,
        var strength: Float = 2f,
    ) : Packet

    override val packetClass = FieldWarpPacket::class

    override fun filter(
        source: BufferedImage
    ): BufferedImage = FieldWarpFilter().filter(source, null)

    override fun filter(
        packet: Packet,
        source: BufferedImage
    ): BufferedImage = FieldWarpFilter().apply {
        if (packet !is FieldWarpPacket) return@apply
        amount = packet.amount
        power = packet.power
        strength = packet.strength
    }.filter(source, null)
}