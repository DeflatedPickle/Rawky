package com.deflatedpickle.rawky.colourwheel

import com.bric.colorpicker.ColorPicker
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.undulation.functions.extensions.toAwt
import com.deflatedpickle.undulation.functions.extensions.toColour
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

object ColourWheelPanel : PluginPanel() {
    val colourPicker = ColorPicker(false, true).apply {
        color = RawkyPlugin.colour

        object : MouseAdapter() {
            fun colour() {
                RawkyPlugin.colour = this@apply.color
            }

            override fun mouseReleased(e: MouseEvent) = colour()
            override fun mouseDragged(e: MouseEvent) = colour()
        }.apply {
            colorPanel.addMouseListener(this)
            colorPanel.addMouseMotionListener(this)
        }
    }

    init {
        add(colourPicker)
    }
}