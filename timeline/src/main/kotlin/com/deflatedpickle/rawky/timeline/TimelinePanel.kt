/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.timeline

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.collection.Frame
import com.deflatedpickle.rawky.dialog.NewFrameDialog
import com.deflatedpickle.rawky.dialog.NewLayerDialog
import com.deflatedpickle.rawky.event.EventChangeFrame
import com.deflatedpickle.rawky.event.EventNewFrame
import com.deflatedpickle.rawky.event.EventNewLayer
import com.deflatedpickle.rawky.event.EventUpdateCell
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.rawky.event.packet.PacketChange
import com.deflatedpickle.rawky.pixelgrid.setting.PixelGridSettings
import com.deflatedpickle.rawky.util.DrawUtil
import com.deflatedpickle.undulation.functions.extensions.add
import org.jdesktop.swingx.JXList
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.BasicStroke
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.BoxLayout
import javax.swing.DefaultListModel
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JToolBar
import javax.swing.ListCellRenderer
import javax.swing.ListSelectionModel
import javax.swing.UIManager
import javax.swing.border.LineBorder

object TimelinePanel : PluginPanel() {
    val toolbar = JToolBar("Timeline").apply {
        orientation = JToolBar.VERTICAL

        add(icon = MonoIcon.ADD_ELEMENT, tooltip = "Add element", enabled = false) {
            RawkyPlugin.document?.let { doc ->
                val frameDialog = NewFrameDialog()
                frameDialog.isVisible = true

                if (frameDialog.result == TaskDialog.StandardCommand.OK) {
                    val layerDialog = NewLayerDialog(0)
                    layerDialog.isVisible = true

                    if (layerDialog.result == TaskDialog.StandardCommand.OK) {
                        val properFrameName = if (frameDialog.nameInput.text == "") null else frameDialog.nameInput.text

                        val frame = doc.addFrame(
                            properFrameName,
                            frameDialog.indexInput.value as Int
                        )

                        model.addElement(frame)

                        EventNewFrame.trigger(frame)

                        val properLayerName = if (layerDialog.nameInput.text == "") null else layerDialog.nameInput.text

                        val layer = frame.addLayer(
                            properLayerName,
                            layerDialog.columnInput.value as Int,
                            layerDialog.rowInput.value as Int,
                            // layerDialog.indexInput.value as Int
                        )

                        list.selectedIndex = frameDialog.indexInput.value as Int

                        EventNewLayer.trigger(layer)
                    }
                }
            }
        }
        // add(icon = MonoIcon.EDIT_ELEMENT, tooltip = "Edit element", enabled = false) {}
        // add(icon = MonoIcon.DELETE_ELEMENT, tooltip = "Delete element", enabled = false) {}
        // add(icon = MonoIcon.DELETE_ALL_ELEMENTS, tooltip = "Delete all elements", enabled = false) {}
    }

    val navbar = JToolBar("Navbar").apply {
        add(icon = MonoIcon.ARROW_LEFT, tooltip = "Decrement frame", enabled = false) {
            RawkyPlugin.document?.let { doc ->
                if (doc.selectedIndex - 1 >= 0) {
                    list.selectedIndex = --doc.selectedIndex
                }
            }
        }

        add(icon = MonoIcon.ARROW_RIGHT, tooltip = "Increment frame", enabled = false) {
            RawkyPlugin.document?.let { doc ->
                if (doc.selectedIndex + 1 < doc.children.size) {
                    list.selectedIndex = ++doc.selectedIndex
                }
            }
        }
    }

    private val listCellRenderer = ListCellRenderer<Frame> { _, value, _, isSelected, _ ->
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
                    if (RawkyPlugin.document == null) return
                    if (RawkyPlugin.document!!.children.size <= 0) return

                    val g2D = g as Graphics2D

                    // TODO: Scale based on the grid size
                    g2D.scale(0.24, 0.24)

                    ConfigUtil.getSettings<PixelGridSettings>("deflatedpickle@pixel_grid#*")?.let { settings ->
                        RawkyPlugin.document?.let {
                            for (layer in value.children.reversed()) {
                                if (layer.visible) {
                                    DrawUtil.paintGrid(
                                        g, layer.child, settings.divide.colour,
                                        BasicStroke(settings.divide.thickness)
                                    )
                                }
                            }
                        }
                    }
                }
            })

            add(
                JLabel((value as Frame).name).apply {
                    alignmentX = JLabel.CENTER_ALIGNMENT
                }
            )
        }
    }

    val model = DefaultListModel<Frame>()
    val list = JXList(model).apply {
        layoutOrientation = JList.HORIZONTAL_WRAP
        visibleRowCount = -1
        selectionMode = ListSelectionModel.SINGLE_SELECTION

        addListSelectionListener {
            RawkyPlugin.document?.let { doc ->
                val oldFrame = doc.children[doc.selectedIndex]

                doc.selectedIndex = selectionModel.anchorSelectionIndex

                val newFrame = doc.children[doc.selectedIndex]
                val layer = newFrame.children[newFrame.selectedIndex]
                val grid = layer.child

                EventUpdateGrid.trigger(grid)
                EventChangeFrame.trigger(
                    PacketChange(
                        System.nanoTime(),
                        PluginUtil.slugToPlugin("deflatedpickle@layer_list")!!,
                        oldFrame,
                        newFrame,
                    )
                )
            }
        }

        cellRenderer = listCellRenderer
    }

    init {
        layout = BorderLayout()

        EventUpdateGrid.addListener {
            list.repaint()
        }

        EventUpdateCell.addListener {
            list.repaint()
        }

        add(toolbar, BorderLayout.WEST)
        add(navbar, BorderLayout.SOUTH)
        add(JScrollPane(list), BorderLayout.CENTER)
    }
}
