/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.colourshades

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.marvin.extensions.darken
import com.deflatedpickle.marvin.extensions.lighten
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.pixelcell.PixelCellPlugin
import com.deflatedpickle.undulation.constraints.FillBothFinishLine
import com.deflatedpickle.undulation.functions.ColourButton
import com.deflatedpickle.undulation.widget.ColourButton
import uk.co.timwise.wraplayout.WrapLayout
import java.awt.GridBagLayout
import javax.swing.JPanel
import javax.swing.JScrollPane

object ColourShadesPanel : PluginPanel() {
    private val colourPanel = JPanel().apply { layout = WrapLayout() }

    init {
        layout = GridBagLayout()
        add(JScrollPane(colourPanel), FillBothFinishLine)
    }

    // Could store buttons in a list and update the instances
    fun createShades() {
        colourPanel.removeAll()

        ConfigUtil.getSettings<ColourShadesSettings>("deflatedpickle@colour_shades#*")?.let { settings,
            ->
            for (i in (0 until settings.darkShades).reversed()) {
                colourPanel.add(
                    ColourButton(
                        PixelCellPlugin.current.darken(
                            i / (settings.darkShades.toDouble() / 2) + 0.1,
                            settings.brightnessStep,
                        ),
                    ) {
                        PixelCellPlugin.current = (it.source as ColourButton).colour
                        EventChangeColour.trigger(PixelCellPlugin.current)
                    },
                )
            }

            colourPanel.add(ColourButton(PixelCellPlugin.current))

            for (i in 0 until settings.lightShades) {
                colourPanel.add(
                    ColourButton(
                        PixelCellPlugin.current.lighten(
                            i / (settings.darkShades.toDouble() / 2) + 0.1,
                            settings.brightnessStep,
                        ),
                    ) {
                        PixelCellPlugin.current = (it.source as ColourButton).colour
                        EventChangeColour.trigger(PixelCellPlugin.current)
                    },
                )
            }
        }

        colourPanel.repaint()
        repaint()
    }
}
