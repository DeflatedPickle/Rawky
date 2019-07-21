package com.deflatedpickle.rawky.components

import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.painter.CompoundPainter
import org.jdesktop.swingx.painter.MattePainter
import uk.co.timwise.wraplayout.WrapLayout
import java.awt.Color
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JPanel

// TODO: Add a context menu to delete colours
class ColourPalette : JPanel() {
    var cellSize = 28

    val newButton = JButton("+").apply {
        preferredSize = Dimension(cellSize, cellSize)

        addActionListener {
            this@ColourPalette.add(
                    JXButton().apply {
                        preferredSize = Dimension(cellSize, cellSize)
                        backgroundPainter = CompoundPainter<JXButton>(MattePainter(Components.colourShades.selectedShade))

                        addActionListener {
                            Components.colourPicker.color = ((backgroundPainter as CompoundPainter<JXButton>).painters[0] as MattePainter).fillPaint as Color
                        }
                    },
                    this@ColourPalette.components.indexOf(this)
            )
        }
    }

    init {
        this.layout = WrapLayout()
        add(newButton)
    }
}