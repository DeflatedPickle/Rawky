/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.colourwheel

import com.bric.colorpicker.ColorPicker
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.rawky.RawkyPlugin

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
