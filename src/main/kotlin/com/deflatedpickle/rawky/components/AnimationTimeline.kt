package com.deflatedpickle.rawky.components

import bibliothek.gui.dock.util.laf.LookAndFeelColors
import com.deflatedpickle.rawky.utils.Components
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.lang.Exception
import javax.swing.*
import javax.swing.border.LineBorder

class AnimationTimeline : JPanel() {
    val listModel = DefaultListModel<String>()
    val list = JList<String>(listModel).apply {
        layoutOrientation = JList.HORIZONTAL_WRAP
        visibleRowCount = -1
        selectionMode = ListSelectionModel.SINGLE_SELECTION

        cellRenderer = ListCellRenderer<String> { list, value, index, isSelected, cellHasFocus ->
            JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)

                if (isSelected) {
                    background = UIManager.getColor("List.selectionBackground")
                    border = LineBorder(UIManager.getColor("List.selectionBackground"), 2)
                }
                else {
                    border = LineBorder(Color.BLACK, 2)
                }

                add(object : JPanel() {
                    init {
                        preferredSize = Dimension(80, 80)
                    }

                    override fun paintComponent(g: Graphics) {
                        val g2D = g as Graphics2D

                        g2D.color = Color.WHITE
                        g2D.fillRect(0, 0, width, height)

                        g2D.scale(0.24, 0.24)

                        // Components.pixelGrid.drawTransparentBackground(g2D)

                        for ((layerIndex, layer) in Components.pixelGrid.frameList[index].layerList.withIndex().reversed()) {
                            Components.pixelGrid.drawPixels(layerIndex, layer, g2D)
                        }
                    }
                })
                add(JLabel(value).apply { alignmentX = JLabel.CENTER_ALIGNMENT })
            }
        }
    }

    init {
        isOpaque = false
        layout = BorderLayout()

        add(list)

        list.addListSelectionListener {
            changeFrame()
        }
    }

    fun addFrame() {
        listModel.addElement("Frame ${listModel.size()}")
        list.selectedIndex = listModel.size() - 1

        Components.pixelGrid.frameList.add(PixelGrid.Frame())
        Components.layerList.addLayer()
    }

    fun changeFrame() {
        try {
            for (i in 0 until Components.layerList.listModel.rowCount) {
                Components.layerList.listModel.removeRow(i)
            }

            for (i in 0 until Components.pixelGrid.frameList[list.selectedIndex].layerList.size) {
                Components.layerList.listModel.insertRow(0, arrayOf(null, "Layer ${Components.layerList.listModel.rowCount}", true, false))
            }

            Components.layerList.list.setRowSelectionInterval(0, 0)
        }
        catch (e: Exception) {
        }
    }
}