package com.deflatedpickle.rawky.pixelgrid.event

import com.deflatedpickle.haruhi.api.event.Packet
import com.deflatedpickle.haruhi.api.plugin.Plugin
import java.io.File

data class PacketReadWriteProgress(
    override val time: Long = System.nanoTime(),
    override val source: Plugin,
    val file: File,
    val progress: Float,
) : Packet