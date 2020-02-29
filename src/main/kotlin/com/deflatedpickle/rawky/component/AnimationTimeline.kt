/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.annotations.RedrawSensitive
import com.deflatedpickle.rawky.api.component.ActionComponent
import com.deflatedpickle.rawky.transfer.RowTransfer
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import com.deflatedpickle.rawky.widget.ColourButton
import com.deflatedpickle.rawky.widget.RangeSlider
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Frame
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.BoxLayout
import javax.swing.DefaultListModel
import javax.swing.DropMode
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.ListCellRenderer
import javax.swing.ListSelectionModel
import javax.swing.UIManager
import javax.swing.border.LineBorder

@RedrawSensitive<PixelGrid>(PixelGrid::class)
class AnimationTimeline : ActionComponent() {
    companion object {
        fun getFrame(index: Int): PixelGrid.Frame = PixelGrid.frameList[index]
        fun getCurrentFrame(): PixelGrid.Frame = PixelGrid.frameList[Components.animationTimeline.list.selectedIndex]
    }

    // TODO: Move to a toolbar sub-class
    val slider = RangeSlider.IntRangeSliderComponent(-10, 10, -2, 2)
    val pastColour = ColourButton(Color.YELLOW).apply { preferredSize = Dimension(32, slider.preferredSize.height) }
    val postColour = ColourButton(Color.MAGENTA).apply { preferredSize = Dimension(32, slider.preferredSize.height) }

    val buttonNew = JButton(Icons.createNew).apply {
        toolTipText = "New Frame"
        addActionListener {
            Components.animationTimeline.addFrame()
        }
    }

    val buttonDelete = JButton(Icons.trash).apply {
        toolTipText = "Delete Frame"
        addActionListener {
            Components.animationTimeline.removeFrame()
        }
    }

    val listModel = DefaultListModel<String>()
    val list = JList<String>(listModel).apply {
        layoutOrientation = JList.HORIZONTAL_WRAP
        visibleRowCount = -1
        selectionMode = ListSelectionModel.SINGLE_SELECTION

        addListSelectionListener { PixelGrid.repaint() }

        cellRenderer = ListCellRenderer<String> { _, value, index, isSelected, _ ->
            JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)

                if (isSelected) {
                    background = UIManager.getColor("List.selectionBackground")
                    border = LineBorder(UIManager.getColor("List.selectionBackground"), 2)
                } else {
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

                        // PixelGrid.drawTransparentBackground(g2D)

                        for ((layerIndex, layer) in PixelGrid.frameList[index].layerList.withIndex().reversed()) {
                            PixelGrid.drawPixels(layerIndex, layer, g2D)
                        }
                    }
                })
                add(JLabel(value).apply { alignmentX = JLabel.CENTER_ALIGNMENT })
            }
        }
    }

    init {
        toolbarWidgets[BorderLayout.PAGE_START] = listOf(
                Pair(pastColour, null),
                Pair(slider, fillX),
                Pair(postColour, null)
        )

        toolbarWidgets[BorderLayout.PAGE_END] = listOf(
                Pair(buttonNew, null),
                Pair(buttonDelete, null)
        )

        list.dragEnabled = true
        list.dropMode = DropMode.INSERT
        list.transferHandler = RowTransfer.ExportImport(list)

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

        PixelGrid.frameList.add(PixelGrid.Frame(listModel.capacity()))
        if (addLayer) {
            Components.layerList.addLayer()
        }

        changeFrame()
    }

    fun removeFrame() {
        with(list.selectedIndex) {
            PixelGrid.frameList.removeAt(this)
            listModel.remove(this)

            if (this > 0) {
                list.addSelectionInterval(this - 1, this - 1)
            } else {
                list.addSelectionInterval(0, 0)
            }
        }

        changeFrame()
    }

    fun changeFrame() {
        Components.layerList.tableModel.rowCount = 0

        if (PixelGrid.frameList.size > list.selectedIndex) {
            if (list.selectedIndex >= 0) {
                for (i in 0 until PixelGrid.frameList[list.selectedIndex].layerList.size) {
                    Components.layerList.tableModel.insertRow(0, arrayOf(null, "Layer ${Components.layerList.tableModel.rowCount}", PixelGrid.frameList[list.selectedIndex].layerList[i].visible, PixelGrid.frameList[list.selectedIndex].layerList[i].lockType))
                }
            }
        }

        try {
            Components.layerList.table.setRowSelectionInterval(0, 0)
        } catch (e: Exception) {
        }
    }
}
