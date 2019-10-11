package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.util.EComponent
import com.deflatedpickle.rawky.util.Components
import java.awt.*
import javax.swing.*
import javax.swing.border.LineBorder

class AnimationTimeline : JPanel() {
    val listModel = DefaultListModel<String>()
    val list = JList<String>(listModel).apply {
        layoutOrientation = JList.HORIZONTAL_WRAP
        visibleRowCount = -1
        selectionMode = ListSelectionModel.SINGLE_SELECTION

        cellRenderer = ListCellRenderer<String> { _, value, index, isSelected, _ ->
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
                            Components.pixelGrid.drawPixels(layerIndex, layer, g2D, EComponent.ANIMATION_TIMELINE)
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
            if (!it.valueIsAdjusting) {
                changeFrame()
            }
        }
    }

    fun addFrame(addLayer: Boolean = true) {
        listModel.addElement("Frame ${listModel.size()}")
        list.selectedIndex = listModel.size() - 1

        Components.pixelGrid.frameList.add(PixelGrid.Frame())
        if (addLayer) {
            Components.layerList.addLayer()
        }

        changeFrame()
    }

    fun removeFrame() {
        with(list.selectedIndex) {
            Components.pixelGrid.frameList.removeAt(this)
            listModel.remove(this)

            if (this > 0) {
                list.addSelectionInterval(this - 1, this - 1)
            }
            else {
                list.addSelectionInterval(0, 0)
            }
        }

        changeFrame()
    }

    fun changeFrame() {
        Components.layerList.listModel.rowCount = 0

        if (Components.pixelGrid.frameList.size > list.selectedIndex) {
            if (list.selectedIndex >= 0) {
                for (i in 0 until Components.pixelGrid.frameList[list.selectedIndex].layerList.size) {
                    Components.layerList.listModel.insertRow(0, arrayOf(null, "Layer ${Components.layerList.listModel.rowCount}", Components.pixelGrid.frameList[list.selectedIndex].layerList[i].visible, Components.pixelGrid.frameList[list.selectedIndex].layerList[i].lockType))
                }
            }
        }

        try {
            Components.layerList.list.setRowSelectionInterval(0, 0)
        }
        catch (e: Exception) {
        }
    }
}