package com.deflatedpickle.rawky.components

import com.deflatedpickle.rawky.utils.Components
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.JMenuItem
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.UIManager

class ColourPalette : JPanel() {
    class ColourSwatch(var x: Int, var y: Int, val colour: Color)

    var cellSize = 40
    val colourList = mutableListOf<ColourSwatch>()

    var mouseX = 0
    var mouseY = 0
    var mouseToggled = false
    var mouseOffsetX = 0
    var mouseOffsetY = 0

    var selectedColour: ColourSwatch? = null

    init {
        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                mouseX = e.x
                mouseY = e.y
            }

            override fun mousePressed(e: MouseEvent) {
                mouseToggled = true

                if (e.button == MouseEvent.BUTTON1) {
                    if (e.clickCount == 2) {
                        Components.colourPicker.color = selectedColour?.colour
                    }
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                mouseToggled = false

                for (i in colourList) {
                    if (e.x > i.x && e.x < i.x + cellSize
                            && e.y > i.y && e.y < i.y + cellSize) {
                        selectedColour = i
                        Collections.swap(colourList, colourList.indexOf(i), colourList.size - 1)
                        mouseOffsetX = e.x - i.x
                        mouseOffsetY = e.y - i.y
                        break
                    } else {
                        selectedColour = null
                        mouseOffsetX = 0
                        mouseOffsetY = 0
                    }
                }
            }

            override fun mouseDragged(e: MouseEvent) {
                if (mouseToggled) {
                    mouseReleased(e)
                }

                if (selectedColour != null) {
                    selectedColour!!.x = e.x - mouseOffsetX
                    selectedColour!!.y = e.y - mouseOffsetY
                }
            }
        }.apply { addMouseListener(this) })

        componentPopupMenu = JPopupMenu().apply {
            add(JMenuItem("Add").apply {
                addActionListener {
                    colourList.add(ColourSwatch(mouseX, mouseY, Components.colourPicker.color))
                }
            })

            add(JMenuItem("Delete").apply {
                addActionListener {
                    for (i in colourList) {
                        if (mouseX > i.x && mouseX < i.x + cellSize
                                && mouseY > i.y && mouseY < i.y + cellSize) {
                            colourList.remove(i)
                            break
                        }
                    }
                }
            })
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2D = g as Graphics2D

        for (i in this.colourList) {
            g2D.color = i.colour
            g2D.fillRect(i.x, i.y, cellSize, cellSize)

            g2D.color = UIManager.getColor("List.selectionBackground")
            val strokeThickness = if (i == selectedColour) {
                3f
            } else if (mouseX > i.x && mouseX < i.x + cellSize
                    && mouseY > i.y && mouseY < i.y + cellSize) {
                2f
            } else {
                0f
            }

            if (strokeThickness > 0) {
                g2D.stroke = BasicStroke(strokeThickness)
                g2D.drawRect(i.x, i.y, cellSize, cellSize)
            }
        }
    }
}