package com.deflatedpickle.rawky.components

import com.deflatedpickle.rawky.Icons
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.painter.CompoundPainter
import org.jdesktop.swingx.painter.MattePainter
import uk.co.timwise.wraplayout.WrapLayout
import java.awt.Color
import java.awt.Dimension
import javax.swing.*
import javax.swing.border.TitledBorder

class ColourLibrary : JPanel() {
    var cellSize = 28
    val cellList = mutableListOf<JXButton>()

    val newButton = JButton(Icons.plus).apply {
        preferredSize = Dimension(cellSize, cellSize)
        toolTipText = "Add Colour"

        addActionListener {
            this@ColourLibrary.add(
                    JXButton().apply {
                        val button = this
                        cellList.add(this)
                        preferredSize = Dimension(cellSize, cellSize)
                        backgroundPainter = CompoundPainter<JXButton>(MattePainter(Components.colourShades.selectedShade))

                        addActionListener {
                            Components.colourPicker.color = ((backgroundPainter as CompoundPainter<JXButton>).painters[0] as MattePainter).fillPaint as Color
                        }

                        // TODO: Add a colour picker to change the cell colour
                        componentPopupMenu = JPopupMenu().apply {
                            add(ColourShades().apply {
                                border = TitledBorder("Change Shade")
                                preferredSize = Dimension(140, 40)

                                colour = ((backgroundPainter as CompoundPainter<JXButton>).painters[0] as MattePainter).fillPaint as Color

                                for ((index, button) in buttonList.withIndex()) {
                                    val shades = this.getShades()
                                    button.addActionListener {
                                        backgroundPainter = CompoundPainter<JXButton>(MattePainter(shades[index]))
                                    }
                                }
                            })
                            add(JSeparator())
                            add(JMenuItem("Delete").apply {
                                addActionListener {
                                    cellList.remove(button)
                                    this@ColourLibrary.remove(button)

                                    this@ColourLibrary.invalidate()
                                    this@ColourLibrary.revalidate()
                                    this@ColourLibrary.repaint()
                                }
                            })
                        }
                    },
                    this@ColourLibrary.components.indexOf(this)
            )

            this.invalidate()
            this.revalidate()
            this.repaint()
        }
    }

    init {
        this.layout = WrapLayout()
        add(newButton)
    }
}