package com.deflatedpickle.rawky.components

import com.deflatedpickle.rawky.utils.Icons
import com.deflatedpickle.rawky.utils.Components
import org.apache.batik.ext.awt.g2d.DefaultGraphics2D
import java.awt.*
import java.util.*
import javax.swing.*
import javax.swing.Timer
import javax.swing.border.LineBorder
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

class LayerList : JPanel() {
    val listModel = DefaultTableModel(arrayOf(), arrayOf("Preview", "Name", "Visibility", "State")).apply {
        addTableModelListener {
            when (it.column) {
                2 -> Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList[it.firstRow].visible = this.getValueAt(it.firstRow, it.column) as Boolean
                3 -> Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList[it.firstRow].locked = this.getValueAt(it.firstRow, it.column) as Boolean
            }
        }
    }
    val list = JTable(listModel).apply {
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
                    return JPanel()
                }

                override fun getCellEditorValue(): Any {
                    return 0
                }

            }
            cellRenderer = TableCellRenderer { table, value, isSelected, hasFocus, row, column ->
                object : JPanel() {
                    init {
                        minimumSize = Dimension(40, 40)
                    }

                    override fun paintComponent(g: Graphics) {
                        val g2D = g as Graphics2D
                        g2D.scale(0.12, 0.12)

                        Components.pixelGrid.drawPixels(row, Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList[row], g2D)
                    }
                }
            }
        }

        columnModel.getColumn(2).apply {
            maxWidth = 30

            cellEditor = DefaultCellEditor(JCheckBox().apply { isVisible = false })
            cellRenderer = TableCellRenderer { _, value, _, _, _, _ ->
                JCheckBox(if (value as Boolean) {
                    Icons.rounded_rectangle_filled
                }
                else {
                    Icons.rounded_rectangle
                }).apply { isOpaque = false }
            }
        }
        columnModel.getColumn(3).apply {
            maxWidth = 30

            cellEditor = DefaultCellEditor(JCheckBox().apply { isVisible = false })
            cellRenderer = TableCellRenderer { _, value, _, _, _, _ ->
                JCheckBox(if (value as Boolean) {
                    Icons.lock
                }
                else {
                    Icons.unlock
                }).apply { isOpaque = false }
            }
        }
    }

    init {
        isOpaque = false
        layout = BorderLayout()

        add(list)
    }

    fun addLayer() {
        listModel.insertRow(0, arrayOf(null, "Layer ${listModel.rowCount}", true, false))
        list.setRowSelectionInterval(0, 0)

        Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList.add(0, PixelGrid.Layer())
    }

    fun removeLayer() {
        Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList.removeAt(list.selectedRow)
        listModel.removeRow(list.selectedRow)
    }
}