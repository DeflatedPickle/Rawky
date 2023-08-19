/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.rawky.layerlist

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.collection.Layer
import com.deflatedpickle.rawky.dialog.EditLayerDialog
import com.deflatedpickle.rawky.dialog.NewLayerDialog
import com.deflatedpickle.rawky.event.EventChangeLayer
import com.deflatedpickle.rawky.event.EventNewLayer
import com.deflatedpickle.rawky.event.EventUpdateCell
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.rawky.event.packet.PacketChange
import com.deflatedpickle.rawky.pixelgrid.api.LayerCategory
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import com.deflatedpickle.rawky.util.DrawUtil
import com.deflatedpickle.undulation.functions.AbstractButton
import com.deflatedpickle.undulation.functions.extensions.add
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.util.EventObject
import javax.swing.AbstractCellEditor
import javax.swing.DefaultCellEditor
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.JToolBar
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer
import kotlin.math.min

// TODO: add reordering of layers
// FIXME: all of it
object LayerListPanel : PluginPanel() {
    val addButton =
        AbstractButton(icon = MonoIcon.ADD_ELEMENT, tooltip = "Add layer", enabled = false) {
            RawkyPlugin.document?.let { doc ->
                val layerDialog = NewLayerDialog()
                layerDialog.isVisible = true

                if (layerDialog.result == TaskDialog.StandardCommand.OK) {
                    val layer =
                        doc.children[doc.selectedIndex].addLayer(
                            layerDialog.nameInput.text,
                            layerDialog.columnInput.value as Int,
                            layerDialog.rowInput.value as Int,
                            // layerDialog.indexInput.value as Int
                        )

                    EventNewLayer.trigger(layer)
                    EventChangeLayer.trigger(
                        PacketChange(
                            System.nanoTime(),
                            PluginUtil.slugToPlugin("deflatedpickle@layer_list")!!,
                            layer,
                            layer,
                        ),
                    )
                }
            }
        }
    val editButton =
        AbstractButton(icon = MonoIcon.EDIT_ELEMENT, tooltip = "Edit element", enabled = false) {
            RawkyPlugin.document?.let { doc ->
                val layerDialog = EditLayerDialog()
                layerDialog.isVisible = true

                if (layerDialog.result == TaskDialog.StandardCommand.OK) {
                    val frame = doc.children[doc.selectedIndex]
                    val layer = frame.children[frame.selectedIndex]

                    layer.name = layerDialog.nameInput.text

                    val grid = layer.child

                    EventUpdateGrid.trigger(grid)
                    EventChangeLayer.trigger(
                        PacketChange(
                            System.nanoTime(),
                            PluginUtil.slugToPlugin("deflatedpickle@layer_list")!!,
                            layer,
                            layer,
                        ),
                    )
                }
            }
        }

    val deleteButton =
        AbstractButton(icon = MonoIcon.DELETE_ELEMENT, tooltip = "Delete element", enabled = false) {
            RawkyPlugin.document?.let { doc ->
                val frame = doc.children[doc.selectedIndex]

                if (frame.selectedIndex > frame.children.size || frame.selectedIndex < 0) return@let

                val layer = frame.children[frame.selectedIndex]

                model.removeRow(frame.selectedIndex)
                frame.children.removeAt(frame.selectedIndex)
                frame.selectedIndex = min(0, frame.selectedIndex--)

                EventChangeLayer.trigger(
                    PacketChange(
                        source = PluginUtil.slugToPlugin("deflatedpickle@layer_list")!!,
                        old = layer,
                        new = doc.children[doc.selectedIndex].children[frame.selectedIndex],
                    ),
                )
            }
        }

    val toolbar =
        JToolBar("Layer list").apply {
            add(addButton)
            add(editButton)
            add(deleteButton)
            // TODO: add a delete all button
        }

    val navbar =
        JToolBar("Navbar").apply {
            orientation = JToolBar.VERTICAL

            add(icon = MonoIcon.ARROW_UP, tooltip = "Decrement layer", enabled = false) {
                RawkyPlugin.document?.let { doc ->
                    val frame = doc.children[doc.selectedIndex]

                    if (frame.selectedIndex - 1 >= 0) {
                        table.setRowSelectionInterval(frame.selectedIndex - 1, frame.selectedIndex - 1)
                    }
                }
            }

            add(icon = MonoIcon.ARROW_DOWN, tooltip = "Increment layer", enabled = false) {
                RawkyPlugin.document?.let { doc ->
                    val frame = doc.children[doc.selectedIndex]

                    if (frame.selectedIndex + 1 < frame.children.size) {
                        table.setRowSelectionInterval(frame.selectedIndex + 1, frame.selectedIndex + 1)
                    }
                }
            }
        }

    val model =
        DefaultTableModel(arrayOf(), arrayOf("Preview", "Name", "Visibility", "Lock")).apply {
            addTableModelListener {
                RawkyPlugin.document?.let { doc ->
                    when (it.column) {
                        2 ->
                            doc.children[doc.selectedIndex].children[it.firstRow].visible =
                                this.getValueAt(it.firstRow, it.column) as Boolean
                        3 ->
                            doc.children[doc.selectedIndex].children[it.firstRow].lock =
                                this.getValueAt(it.firstRow, it.column) as Boolean
                    }

                    EventUpdateGrid.trigger(doc.children[doc.selectedIndex].children[it.firstRow].child)
                }
            }
        }

    private val columnZeroRenderer =
        TableCellRenderer { _: JTable, value: Any?, _: Boolean, _: Boolean, row: Int, _: Int ->
            object : JPanel() {
                override fun paintComponent(g: Graphics) {
                    val doc = RawkyPlugin.document ?: return
                    val l = value as Layer

                    val g2D = g as Graphics2D
                    g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)

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
                        it.layer == LayerCategory.GRID || it.layer == LayerCategory.BACKGROUND
                    }
                    ) {
                        v.paint(doc, doc.selectedIndex, doc.children[doc.selectedIndex].children.indexOf(l), g2D)
                    }
                }
            }
        }
    private val panelEditor: TableCellEditor =
        object : AbstractCellEditor(), TableCellEditor {
            override fun isCellEditable(e: EventObject) = false

            override fun getTableCellEditorComponent(
                table: JTable,
                value: Any?,
                isSelected: Boolean,
                row: Int,
                column: Int,
            ) = JPanel() as Component

            override fun getCellEditorValue() = 0
        }
    private val columnTwoEditor = DefaultCellEditor(JCheckBox().apply { isVisible = false })
    private val columnTwoRenderer = TableCellRenderer { _, value, _, _, _, _ ->
        JCheckBox(if (value as Boolean) MonoIcon.SHOW else MonoIcon.HIDE).apply { isOpaque = false }
    }
    private val columnThreeEditor = DefaultCellEditor(JCheckBox().apply { isVisible = false })
    private val columnThreeRenderer = TableCellRenderer { _, value, _, _, _, _ ->
        JCheckBox(if (value as Boolean) MonoIcon.LOCK_CLOSED else MonoIcon.LOCK_OPEN).apply {
            isOpaque = false
        }
    }

    val table =
        JTable(model).apply {
            showVerticalLines = false
            tableHeader = null
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION)

            rowHeight = 40

            selectionModel.addListSelectionListener {
                if (!it.valueIsAdjusting) {
                    RawkyPlugin.document?.let { doc ->
                        val frame = doc.children[doc.selectedIndex]

                        val oldLayer = frame.children[frame.selectedIndex]
                        frame.selectedIndex =
                            min(frame.children.lastIndex, selectionModel.anchorSelectionIndex)

                        val newLayer = frame.children[frame.selectedIndex]
                        val grid = newLayer.child

                        EventUpdateGrid.trigger(grid)
                        EventChangeLayer.trigger(
                            PacketChange(
                                System.nanoTime(),
                                PluginUtil.slugToPlugin("deflatedpickle@layer_list")!!,
                                oldLayer,
                                newLayer,
                            ),
                        )
                    }
                }
            }

            columnModel.apply {
                getColumn(0).apply {
                    maxWidth = 40

                    cellEditor = panelEditor
                    cellRenderer = columnZeroRenderer
                }

                getColumn(1).apply { cellEditor = panelEditor }

                getColumn(2).apply {
                    maxWidth = 30

                    cellEditor = columnTwoEditor
                    cellRenderer = columnTwoRenderer
                }

                getColumn(3).apply {
                    maxWidth = 30

                    cellEditor = columnThreeEditor
                    cellRenderer = columnThreeRenderer
                }
            }
        }

    init {
        layout = BorderLayout()

        EventUpdateGrid.addListener { table.repaint() }

        EventUpdateCell.addListener { table.repaint() }

        add(toolbar, BorderLayout.SOUTH)
        add(navbar, BorderLayout.EAST)
        add(JScrollPane(table), BorderLayout.CENTER)
    }
}
