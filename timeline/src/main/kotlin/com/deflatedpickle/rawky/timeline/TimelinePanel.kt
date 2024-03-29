/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.rawky.timeline

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.collection.Frame
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.dialog.EditFrameDialog
import com.deflatedpickle.rawky.dialog.NewFrameDialog
import com.deflatedpickle.rawky.dialog.NewLayerDialog
import com.deflatedpickle.rawky.event.EventChangeFrame
import com.deflatedpickle.rawky.event.EventNewFrame
import com.deflatedpickle.rawky.event.EventNewLayer
import com.deflatedpickle.rawky.event.EventUpdateCell
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.rawky.event.packet.PacketChange
import com.deflatedpickle.rawky.pixelgrid.api.LayerCategory
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import com.deflatedpickle.rawky.util.DrawUtil
import com.deflatedpickle.undulation.functions.AbstractButton
import org.jdesktop.swingx.JXList
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
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
import kotlin.math.min

// TODO: add reordering of frames
object TimelinePanel : PluginPanel() {
    val addButton =
        AbstractButton(
            icon = MonoIcon.ADD_ELEMENT,
            tooltip = "Add frame",
            enabled = false
        ) {
            RawkyPlugin.document?.let { doc ->
                val frameDialog = NewFrameDialog()
                frameDialog.isVisible = true

                if (frameDialog.result == TaskDialog.StandardCommand.OK) {
                    val layerDialog = NewLayerDialog(0)
                    layerDialog.isVisible = true

                    if (layerDialog.result == TaskDialog.StandardCommand.OK) {
                        val properFrameName =
                            if (frameDialog.nameInput.text == "") null else frameDialog.nameInput.text

                        val frame = doc.addFrame(properFrameName, frameDialog.indexInput.value as Int)

                        EventNewFrame.trigger(frame)

                        val properLayerName = layerDialog.nameInput.text.ifBlank { null }

                        val layer =
                            frame.addLayer(
                                properLayerName,
                                layerDialog.columnInput.value as Int,
                                layerDialog.rowInput.value as Int,
                                // layerDialog.indexInput.value as Int
                            )

                        EventNewLayer.trigger(layer)
                        EventChangeFrame.trigger(
                            PacketChange(
                                System.nanoTime(),
                                PluginUtil.slugToPlugin("deflatedpickle@layer_list")!!,
                                frame,
                                frame,
                            ),
                        )
                    }
                }
            }
        }
    val editButton =
        AbstractButton(icon = MonoIcon.EDIT_ELEMENT, tooltip = "Edit element", enabled = false) {
            RawkyPlugin.document?.let { doc ->
                val frameDialog = EditFrameDialog()
                frameDialog.isVisible = true

                if (frameDialog.result == TaskDialog.StandardCommand.OK) {
                    val frame = doc.children[doc.selectedIndex]

                    frame.name = frameDialog.nameInput.text

                    val layer = frame.children[frame.selectedIndex]
                    val grid = layer.child

                    EventUpdateGrid.trigger(grid)
                    EventChangeFrame.trigger(
                        PacketChange(
                            System.nanoTime(),
                            PluginUtil.slugToPlugin("deflatedpickle@layer_list")!!,
                            frame,
                            frame,
                        ),
                    )
                }
            }
        }
    val deleteButton =
        AbstractButton(icon = MonoIcon.DELETE_ELEMENT, tooltip = "Delete element", enabled = false) {
            RawkyPlugin.document?.let { doc ->
                if (doc.selectedIndex > doc.children.size || doc.selectedIndex < 0) return@let

                val frame = doc.children[doc.selectedIndex]

                model.remove(doc.selectedIndex)
                doc.children.removeAt(doc.selectedIndex)
                doc.selectedIndex = min(0, doc.selectedIndex--)

                EventChangeFrame.trigger(
                    PacketChange(
                        source = PluginUtil.slugToPlugin("deflatedpickle@layer_list")!!,
                        old = frame,
                        new = doc.children[doc.selectedIndex],
                    ),
                )
            }
        }

    val toolbar =
        JToolBar("Timeline").apply {
            orientation = JToolBar.VERTICAL

            add(addButton)
            add(editButton)
            add(deleteButton)
            // add(icon = MonoIcon.DELETE_ALL_ELEMENTS, tooltip = "Delete all elements", enabled =
            // false) {}
        }

    val firstButton = AbstractButton(icon = MonoIcon.ARROW_START_HORIZONTAL, tooltip = "First Frame", enabled = false) {
        RawkyPlugin.document?.let {
            list.selectedIndex = 0
            TimelinePlugin.triggerNavButtons()
        }
    }

    val decrementButton = AbstractButton(icon = MonoIcon.ARROW_LEFT, tooltip = "Decrement Frame", enabled = false) {
        RawkyPlugin.document?.let { doc ->
            if (doc.selectedIndex - 1 >= 0) {
                list.selectedIndex = --doc.selectedIndex
            }

            TimelinePlugin.triggerNavButtons()
        }
    }

    val incrementButton = AbstractButton(icon = MonoIcon.ARROW_RIGHT, tooltip = "Increment Frame", enabled = false) {
        RawkyPlugin.document?.let { doc ->
            if (doc.selectedIndex + 1 < doc.children.size) {
                list.selectedIndex = ++doc.selectedIndex
            }

            TimelinePlugin.triggerNavButtons()
        }
    }

    val lastButton = AbstractButton(icon = MonoIcon.ARROW_END_HORIZONTAL, tooltip = "Last Frame", enabled = false) {
        RawkyPlugin.document?.let {
            list.selectedIndex = list.model.size - 1
            TimelinePlugin.triggerNavButtons()
        }
    }

    val navbar =
        JToolBar("Navbar").apply {
            add(firstButton)
            add(decrementButton)
            add(incrementButton)
            add(lastButton)
        }

    private val listCellRenderer =
        ListCellRenderer<Frame> { _, value, index, isSelected, _ ->
            JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)

                if (isSelected) {
                    background = UIManager.getColor("List.selectionBackground")
                    border = LineBorder(UIManager.getColor("List.selectionBackground"), 2)
                } else {
                    border = LineBorder(UIManager.getColor("List.background"), 2)
                }

                add(
                    object : JPanel() {
                        init {
                            preferredSize = Dimension(80, 80)
                        }

                        override fun paintComponent(g: Graphics) {
                            if (RawkyPlugin.document == null) return
                            if (RawkyPlugin.document!!.children.size <= 0) return

                            val g2D = g as Graphics2D
                            g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)

                            RawkyPlugin.document?.let { doc ->
                                val factor = DrawUtil.getScaleFactor(
                                    width.toDouble() / Grid.pixel,
                                    height.toDouble() / Grid.pixel,
                                    doc.columns.toDouble(),
                                    doc.rows.toDouble(),
                                )
                                g2D.scale(factor, factor)

                                for (
                                v in
                                PaintLayer.registry.getAll().values.filter {
                                    it.layer == LayerCategory.BACKGROUND
                                }
                                ) {
                                    v.paint(doc, index, -1, g2D)
                                }

                                for ((l, layer) in value.children.reversed().withIndex()) {
                                    if (layer.visible) {
                                        for (
                                        v in
                                        PaintLayer.registry.getAll().values.filter {
                                            it.layer == LayerCategory.GRID
                                        }
                                        ) {
                                            v.paint(doc, index, l, g2D)
                                        }
                                    }
                                }
                            }
                        }
                    },
                )

                add(JLabel((value as Frame).name).apply { alignmentX = JLabel.CENTER_ALIGNMENT })
            }
        }

    val model = DefaultListModel<Frame>()
    val list =
        JXList(model).apply {
            layoutOrientation = JList.HORIZONTAL_WRAP
            visibleRowCount = -1
            selectionMode = ListSelectionModel.SINGLE_SELECTION

            addListSelectionListener {
                RawkyPlugin.document?.let { doc ->
                    if (doc.selectedIndex < 0 || doc.selectedIndex >= doc.children.size) return@let

                    val oldFrame = doc[doc.selectedIndex]

                    doc.selectedIndex = min(doc.children.size - 1, selectionModel.anchorSelectionIndex)

                    if (doc.selectedIndex < 0 || doc.selectedIndex < doc.children.size) return@let

                    val newFrame = doc[doc.selectedIndex]
                    val layer = newFrame.children[newFrame.selectedIndex]
                    val grid = layer.child

                    EventUpdateGrid.trigger(grid)
                    EventChangeFrame.trigger(
                        PacketChange(
                            System.nanoTime(),
                            PluginUtil.slugToPlugin("deflatedpickle@layer_list")!!,
                            oldFrame,
                            newFrame,
                        ),
                    )

                    TimelinePlugin.triggerNavButtons()
                }
            }

            cellRenderer = listCellRenderer
        }

    init {
        layout = BorderLayout()

        EventUpdateGrid.addListener { list.repaint() }

        EventUpdateCell.addListener { list.repaint() }

        EventChangeFrame.addListener {
            RawkyPlugin.document?.let { doc -> deleteButton.isEnabled = doc.children.size > 1 }
        }

        add(toolbar, BorderLayout.WEST)
        add(navbar, BorderLayout.SOUTH)
        add(JScrollPane(list), BorderLayout.CENTER)
    }
}
