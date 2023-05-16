/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tilepalette

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.rawky.api.palette.Palette
import com.deflatedpickle.rawky.api.palette.PaletteParser
import com.deflatedpickle.rawky.tilecell.TileCellPlugin
import com.deflatedpickle.undulation.constraints.FillBothFinishLine
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.widget.ImageButton
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.WrapLayout
import so.n0weak.ExtendedComboBox
import java.awt.GridBagLayout
import java.awt.Image
import java.awt.event.ItemEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JScrollPane

object TilePalettePanel : PluginPanel() {
    val combo = ExtendedComboBox().apply {
        addItemListener {
            when (it.stateChange) {
                ItemEvent.SELECTED -> {
                    tilePanel.removeAll()

                    if (this.selectedItem is Palette<*>) {
                        val palette = this.selectedItem as Palette<BufferedImage>

                        for (i in palette.items) {
                            tilePanel.add(
                                ImageButton(i.key).apply {
                                    toolTipText = i.value

                                    addActionListener {
                                        TileCellPlugin.current = i.key
                                    }
                                }
                            )
                        }
                    }

                    tilePanel.validate()
                    tilePanel.repaint()
                }
            }
        }
    }

    private val tilePanel = JXPanel().apply {
        layout = WrapLayout()
    }

    init {
        layout = GridBagLayout()
        add(combo, FillHorizontalFinishLine)
        add(JScrollPane(tilePanel), FillBothFinishLine)

        /*EventProgramFinishSetup.addListener {
            for (i in TilePalettePlugin.folder.walk()) {
                if (i.isFile) {
                    TilePalettePlugin.registry[i.extension]?.let { pp ->
                        combo.addItem(pp.parse(i))
                    }
                } else if (i.isDirectory && i.name != "palette") {
                    combo.addDelimiter(i.name)
                }
            }
        }*/
    }
}
