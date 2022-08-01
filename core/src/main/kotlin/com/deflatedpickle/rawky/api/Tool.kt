package com.deflatedpickle.rawky.api

import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkySettings
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.event.EventChangeTool
import kotlinx.serialization.ExperimentalSerializationApi
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Image
import java.awt.Point
import java.awt.Toolkit
import javax.swing.ImageIcon

abstract class Tool(
    val name: String,
    val icon: ImageIcon,
) {
    companion object {
        val registry = Registry<String, Tool>()

        const val defaultSize = 32
        lateinit var current: Tool

        init {
            EventCreateDocument.addListener {
                current = registry.values.first()
                EventChangeTool.trigger(current)
            }

            EventOpenDocument.addListener {
                current = registry.values.first()
                EventChangeTool.trigger(current)
            }
        }

        fun isToolValid() = this::current.isInitialized
    }

    abstract fun perform(
        cell: Cell,
        button: Int,
        dragged: Boolean,
        clickCount: Int,
    )

    override fun toString() = name
}