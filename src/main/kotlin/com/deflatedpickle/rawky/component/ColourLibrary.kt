/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.component.Component
import com.deflatedpickle.rawky.transfer.ColourTransfer
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import java.awt.Color
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.JSeparator
import javax.swing.border.TitledBorder
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.painter.CompoundPainter
import org.jdesktop.swingx.painter.MattePainter
import uk.co.timwise.wraplayout.WrapLayout
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.awt.dnd.DnDConstants
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.TransferHandler

class ColourLibrary : Component() {
    class Cell(val colour: Color, val button: JXButton)

    var cellSize = 28
    val cellList = mutableListOf<Cell>()

    val newButton = JButton(Icons.addElement).apply {
        preferredSize = Dimension(cellSize, cellSize)
        toolTipText = "Add Colour"

        addActionListener {
            addButton()
        }
    }

    init {
        transferHandler = ColourTransfer.Import

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

            ColourTransfer.pressedExport(this, colour)

            // TODO: Add a colour picker to change the cell colour
            componentPopupMenu = JPopupMenu().apply {
                add(ColourShades().apply {
                    border = TitledBorder("Change Shade")
                    preferredSize = Dimension(140, 40)

                    // this.colour = ((backgroundPainter as CompoundPainter<JXButton>).painters[0] as MattePainter).fillPaint as Color

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

                        this@ColourLibrary.repaint()
                    }
                })
            }
        }, this@ColourLibrary.components.indexOf(this))

        this.repaint()
    }
}
