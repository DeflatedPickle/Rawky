package com.deflatedpickle.rawky.colourpalette

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.colourpalette.api.Palette
import com.deflatedpickle.rawky.colourpalette.api.PaletteParser
import com.deflatedpickle.undulation.constraints.FillBothFinishLine
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.widget.ColourButton
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.WrapLayout
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import javax.swing.JComboBox
import javax.swing.JScrollPane


object ColourPalettePanel : PluginPanel() {
    private val combo = JComboBox<Palette>().apply {
        addItemListener {
            when (it.stateChange) {
                ItemEvent.SELECTED -> {
                    colourPanel.removeAll()

                    for (i in (this.selectedItem as Palette).colours) {
                        colourPanel.add(
                            ColourButton(i.key).apply {
                                toolTipText = i.value

                                addActionListener {
                                    RawkyPlugin.colour = i.key
                                }
                            }
                        )
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
                    PaletteParser.registry[i.extension]?.let { pp ->
                        combo.addItem(pp.parse(i))
                    }
                }
            }
        }
    }
}