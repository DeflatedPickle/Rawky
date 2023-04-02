/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.event.packet

import com.deflatedpickle.haruhi.api.event.Packet
import com.deflatedpickle.haruhi.api.plugin.Plugin

data class PacketChange<T>(
    override val time: Long = System.nanoTime(),
    override val source: Plugin,
    val old: T,
    val new: T,
) : Packet
