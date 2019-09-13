package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.util.Icons
import com.deflatedpickle.rawky.util.Components
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.painter.CompoundPainter
import org.jdesktop.swingx.painter.MattePainter
import uk.co.timwise.wraplayout.WrapLayout
import java.awt.Color
import java.awt.Dimension
import javax.swing.*
import javax.swing.border.TitledBorder

class ColourLibrary : JPanel() {
    class Cell(val colour: Color, val button: JXButton)

    var cellSize = 28
    val cellList = mutableListOf<Cell>()

    val newButton = JButton(Icons.add_element).apply {
        preferredSize = Dimension(cellSize, cellSize)
        toolTipText = "Add Colour"

        addActionListener {
            addButton()
        }
    }

    init {
        this.layout = WrapLayout()
        add(newButton)
    }

    fun addButton(colour: Color = Components.colourShades.selectedShade) {
        add(JXButton().apply {
            val cell = Cell(colour, this)
            cellList.add(cell)
            preferredSize = Dimension(cellSize, cellSize)
            backgroundPainter = CompoundPainter<JXButton>(MattePainter(colour))

            addActionListener {
                Components.colourPicker.color = ((backgroundPainter as CompoundPainter<JXButton>).painters[0] as MattePainter).fillPaint as Color
            }

            // TODO: Add a colour picker to change the cell colour
            componentPopupMenu = JPopupMenu().apply {
                add(ColourShades().apply {
                    border = TitledBorder("Change Shade")
                    preferredSize = Dimension(140, 40)

                    this.colour = ((backgroundPainter as CompoundPainter<JXButton>).painters[0] as MattePainter).fillPaint as Color

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
                        cellList.remove(cell)
                        this@ColourLibrary.remove(cell.button)

                        this@ColourLibrary.invalidate()
                        this@ColourLibrary.revalidate()
                        this@ColourLibrary.repaint()
                    }
                })
            }
        }, this@ColourLibrary.components.indexOf(this))

        this.invalidate()
        this.revalidate()
        this.repaint()
    }
}