/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.event.packet

import com.deflatedpickle.haruhi.api.event.Packet
import com.deflatedpickle.haruhi.api.plugin.Plugin

data class PacketModify<T>(
    override val time: Long = System.nanoTime(),
    override val source: Plugin,
    val value: T,
    val index: Int,
) : Packet
