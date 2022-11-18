package com.deflatedpickle.rawky.event.packet

import com.deflatedpickle.haruhi.api.event.Packet
import com.deflatedpickle.haruhi.api.plugin.Plugin

data class PacketChange<T>(
    override val time: Long,
    override val source: Plugin,
    val old: T,
    val new: T,
) : Packet