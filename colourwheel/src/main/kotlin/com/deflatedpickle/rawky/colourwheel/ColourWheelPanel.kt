/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.colourwheel

import com.bric.colorpicker.ColorPicker
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.rawky.colourwheel.component.TwoColourButton
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.pixelcell.PixelCellPlugin
import java.awt.BorderLayout
import javax.swing.JToolBar

object ColourWheelPanel : PluginPanel() {
    val colourPicker =
        ColorPicker(false, true).apply {
            color = PixelCellPlugin.current

            addColorListener {
                PixelCellPlugin.current = it.color
                EventChangeColour.trigger(it.color)
            }
        }

    val colourSelector = TwoColourButton()

    val toolbar = JToolBar().apply { add(colourSelector) }

    init {
        layout = BorderLayout()

        add(colourPicker, BorderLayout.CENTER)
        add(toolbar, BorderLayout.EAST)
    }
}
