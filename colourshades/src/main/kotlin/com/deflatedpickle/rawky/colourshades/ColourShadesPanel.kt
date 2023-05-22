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
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.WrapLayout
import java.awt.GridBagLayout
import javax.swing.JScrollPane

object ColourShadesPanel : PluginPanel() {
    private val colourPanel = JXPanel().apply {
        layout = WrapLayout()
    }

    init {
        layout = GridBagLayout()
        add(JScrollPane(colourPanel), FillBothFinishLine)
    }

    // Could store buttons in a list and update the instances
    fun createShades() {
        colourPanel.removeAll()

        ConfigUtil.getSettings<ColourShadesSettings>("deflatedpickle@colour_shades#*")?.let { settings ->
            for (i in (0 until settings.darkShades).reversed()) {
                colourPanel.add(
                    ColourButton(
                        PixelCellPlugin
                            .current
                            .darken(i / (settings.darkShades.toDouble() / 2) + 0.1,
                                settings.brightnessStep
                            )
                    ) {
                        PixelCellPlugin.current = it.color
                        EventChangeColour.trigger(it.color)
                    }
                )
            }

            colourPanel.add(ColourButton(PixelCellPlugin.current))

            for (i in 0 until settings.lightShades) {
                colourPanel.add(
                    ColourButton(
                        PixelCellPlugin
                            .current
                            .lighten(i / (settings.darkShades.toDouble() / 2) + 0.1,
                                settings.brightnessStep
                            )
                    ) {
                        PixelCellPlugin.current = it.color
                        EventChangeColour.trigger(it.color)
                    }
                )
            }
        }

        colourPanel.repaint()
        repaint()
    }
}
