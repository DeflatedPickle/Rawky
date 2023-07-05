/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.colourwheel

import com.bric.colorpicker.ColorPicker
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.pixelcell.PixelCellPlugin
import java.awt.BorderLayout

object ColourWheelPanel : PluginPanel() {
    val colourPicker = ColorPicker(true, true).apply {
        color = PixelCellPlugin.current

        addColorListener {
            PixelCellPlugin.current = it.color
            EventChangeColour.trigger(it.color)
        }
    }

    init {
        layout = BorderLayout()

        add(colourPicker)
    }
}
