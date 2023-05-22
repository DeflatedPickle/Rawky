/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.colourpalette

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.palette.Palette
import com.deflatedpickle.rawky.api.palette.PaletteParser
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.pixelcell.PixelCellPlugin
import com.deflatedpickle.undulation.constraints.FillBothFinishLine
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.widget.ColourButton
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.WrapLayout
import so.n0weak.ExtendedComboBox
import java.awt.Color
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import javax.swing.JScrollPane

object ColourPalettePanel : PluginPanel() {
    val combo = ExtendedComboBox().apply {
        addItemListener {
            when (it.stateChange) {
                ItemEvent.SELECTED -> {
                    colourPanel.removeAll()

                    if (this.selectedItem is Palette<*>) {
                        for (i in (this.selectedItem as Palette<Color>).items) {
                            colourPanel.add(
                                ColourButton(i.key).apply {
                                    toolTipText = i.value

                                    addActionListener {
                                        PixelCellPlugin.current = i.key
                                        EventChangeColour.trigger(i.key)
                                    }
                                }
                            )
                        }
                    }

                    colourPanel.validate()
                    colourPanel.repaint()
                }
            }
        }
    }

    private val colourPanel = JXPanel().apply {
        layout = WrapLayout()
    }

    init {
        layout = GridBagLayout()
        add(combo, FillHorizontalFinishLine)
        add(JScrollPane(colourPanel), FillBothFinishLine)

        EventProgramFinishSetup.addListener {
            for (i in ColourPalettePlugin.folder.walk()) {
                if (i.isFile) {
                    ColourPalettePlugin.registry[i.extension]?.let { pp ->
                        combo.addItem(pp.parse(i))
                    }
                } else if (i.isDirectory && i.name != "palette") {
                    combo.addDelimiter(i.name)
                }
            }
        }
    }
}
