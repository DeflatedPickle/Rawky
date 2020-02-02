/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.component

import com.alexandriasoftware.swing.JSplitButton
import com.deflatedpickle.rawky.api.annotations.RedrawSensitive
import com.deflatedpickle.rawky.api.component.Component
import com.deflatedpickle.rawky.transfer.RowTransfer
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import com.deflatedpickle.rawky.util.VerticalDirection
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.util.EventObject
import java.util.Vector
import javax.swing.AbstractCellEditor
import javax.swing.DefaultCellEditor
import javax.swing.DefaultComboBoxModel
import javax.swing.DropMode
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JMenuItem
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

@RedrawSensitive<PixelGrid>(PixelGrid::class)
class LayerList : Component() {
    val addButton = JButton(Icons.createNew).apply {
        toolTipText = "New Layer"
        addActionListener {
            Components.layerList.addLayer()
        }
    }

    val deleteButton = JButton(Icons.trash).apply {
        toolTipText = "Delete Layer"
        addActionListener {
            Components.layerList.removeLayer()
        }
    }

    val mergeButton = JSplitButton("Merge").apply {
        preferredSize = Dimension(80, 20)

        popupMenu = (JPopupMenu().apply {
            add(JMenuItem("Up").apply {
                addActionListener { mergeLayers(VerticalDirection.NORTH) }
            })
            add(JMenuItem("Down").apply {
                addActionListener { mergeLayers(VerticalDirection.SOUTH) }
            })
        })
    }

    val tableModel = DefaultTableModel(arrayOf(), arrayOf("Preview", "Name", "Visibility", "State")).apply {
        addTableModelListener {
            when (it.column) {
                2 -> PixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList[it.firstRow].visible = this.getValueAt(it.firstRow, it.column) as Boolean
                3 -> PixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList[it.firstRow].lockType = this.getValueAt(it.firstRow, it.column) as PixelGrid.Layer.LockType
            }
        }
    }

    val table = JTable(tableModel).apply {
        autoResizeMode = JTable.AUTO_RESIZE_OFF
        showVerticalLines = false
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)

        rowHeight = 40

        columnModel.getColumn(0).apply {
            maxWidth = 40

            cellEditor = object : AbstractCellEditor(), TableCellEditor {
                override fun isCellEditable(e: EventObject?): Boolean {
                    return false
                }

                override fun getTableCellEditorComponent(table: JTable?, value: Any?, isSelected: Boolean, row: Int, column: Int): Component {
                    return JPanel() as Component
                }

                override fun getCellEditorValue(): Any {
                    return 0
                }
            }
            cellRenderer = TableCellRenderer { _, _, _, _, row, _ ->
                object : JPanel() {
                    init {
                        minimumSize = Dimension(40, 40)
                    }

                    override fun paintComponent(g: Graphics) {
                        val g2D = g as Graphics2D

                        g2D.color = Color.WHITE
                        g2D.fillRect(0, 0, width, height)

                        g2D.scale(0.12, 0.12)

                        // PixelGrid.drawTransparentBackground(g2D)

                        // BUG: Throws an out-of-bounds error when you swap from a frame with more layers
                        PixelGrid.drawPixels(row, PixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList[row], g2D, true, true)
                    }
                }
            }
        }

        columnModel.getColumn(2).apply {
            maxWidth = 30

            cellEditor = DefaultCellEditor(JCheckBox().apply {
                isVisible = false

                addChangeListener { PixelGrid.repaintWithChildren() }
            })
            cellRenderer = TableCellRenderer { _, value, _, _, _, _ ->
                JCheckBox(if (value as Boolean) {
                    Icons.show
                } else {
                    Icons.hide
                }).apply { isOpaque = false }
            }
        }
        columnModel.getColumn(3).apply {
            maxWidth = 90

            val cellModel = DefaultComboBoxModel(PixelGrid.Layer.LockType.values())
            cellEditor = DefaultCellEditor(JComboBox(cellModel))
        }
    }

    init {
        isOpaque = false
        layout = BorderLayout()

        toolbarWidgets[BorderLayout.PAGE_END] = listOf(
                Pair(addButton, null),
                Pair(deleteButton, null),
                Pair(mergeButton, null)
        )

        table.dragEnabled = true
        table.dropMode = DropMode.INSERT_ROWS
        table.transferHandler = RowTransfer.ExportImport(table)

        add(table)
    }

    fun addLayer() {
        tableModel.insertRow(0, arrayOf(null, "Layer ${tableModel.rowCount}", true, PixelGrid.Layer.LockType.OFF))
        table.setRowSelectionInterval(0, 0)

        with(PixelGrid.frameList[Components.animationTimeline.list.selectedIndex]) {
            layerList.add(0, PixelGrid.Layer(this))
        }
    }

    fun removeLayer() {
        with(table.selectedRow) {
            PixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList.removeAt(this)
            tableModel.removeRow(this)

            if (this < 0) {
                table.setRowSelectionInterval(this, this)
            } else {
                if (this - 1 > 0) {
                    table.setRowSelectionInterval(this - 1, this - 1)
                }
            }
        }
    }

    fun isLayerHidden(index: Int = table.selectedRow): Boolean {
        return if (index >= table.rowCount) {
            false
        } else {
            !((tableModel.dataVector[index] as Vector<*>)[2] as Boolean)
        }
    }

    fun layerLockType(index: Int = table.selectedRow): PixelGrid.Layer.LockType {
        return (tableModel.dataVector[index] as Vector<*>)[3] as PixelGrid.Layer.LockType
    }

    fun mergeLayers(direction: VerticalDirection) {
        for (row in 0 until PixelGrid.rowAmount) {
            for (column in 0 until PixelGrid.columnAmount) {
                val cell = PixelGrid.frameList[Components.animationTimeline.list.selectedIndex]
                        .layerList[table.selectedRow]
                        .pixelMatrix[row][column]

                if (cell.colour != null) {
                    PixelGrid.frameList[Components.animationTimeline.list.selectedIndex]
                            .layerList[table.selectedRow + if (direction == VerticalDirection.SOUTH) 1 else -1]
                            .pixelMatrix[row][column] = cell
                }
            }
        }

        removeLayer()
        PixelGrid.repaintWithChildren()
    }
}
