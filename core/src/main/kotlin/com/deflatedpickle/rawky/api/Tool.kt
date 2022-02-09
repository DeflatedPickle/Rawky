package com.deflatedpickle.rawky.api

import com.deflatedpickle.haruhi.event.EventCreateDocument
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

interface Tool {
    companion object {
        val registry = Registry<String, Tool>()

        val bestSize: Dimension = Toolkit.getDefaultToolkit().getBestCursorSize(0, 0)

        const val defaultSize = 24
        lateinit var current: Tool

        init {
            EventCreateDocument.addListener {
                current = registry.values.first()
                EventChangeTool.trigger(current)
            }
        }
    }

    val name: String
    val icon: ImageIcon

    fun perform(
        cell: Cell,
        button: Int,
        dragged: Boolean,
        clickCount: Int,
    )

    @ExperimentalSerializationApi
    fun asCursor(): Cursor {
        val settings = ConfigUtil.getSettings<RawkySettings>("deflatedpickle@core#*")
        val x = settings?.cursorSize?.x ?: defaultSize
        val y = settings?.cursorSize?.y ?: defaultSize

        return Toolkit.getDefaultToolkit().createCustomCursor(
            icon.image.getScaledInstance(
                x,
                y,
                Image.SCALE_AREA_AVERAGING
            ),
            when (icon) {
                MonoIcon.PENCIL -> Point(
                    2,
                    y - 1,
                )
                MonoIcon.ERASER -> Point(
                    4,
                    y - 2
                )
                else -> Point(
                    bestSize.width / 2,
                    bestSize.height / 2,
                )
            },
            name,
        )
    }
}