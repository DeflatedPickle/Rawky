package com.deflatedpickle.rawky.colourwheel

import com.bric.colorpicker.ColorPicker
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.undulation.functions.extensions.toAwt
import com.deflatedpickle.undulation.functions.extensions.toColour
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

object ColourWheelPanel : PluginPanel() {
    val colourPicker = ColorPicker(true, true).apply {
        color = RawkyPlugin.colour

        addColorListener {
            RawkyPlugin.colour = it.color
        }
    }

    init {
        add(colourPicker)
    }
}