package com.deflatedpickle.rawky.components

import com.deflatedpickle.rawky.utils.Icons
import com.deflatedpickle.rawky.utils.Components
import java.awt.BorderLayout
import javax.swing.*
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellRenderer

class LayerList : JPanel() {
    val listModel = DefaultTableModel(arrayOf(), arrayOf("Name", "Visibility", "State"))
    val list = JTable(listModel).apply {
        autoResizeMode = JTable.AUTO_RESIZE_OFF
        showVerticalLines = false

        rowHeight = 24

        columnModel.getColumn(1).apply {
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
        columnModel.getColumn(2).apply {
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

        add(JToolBar().apply {
            add(JButton(Icons.create_new).apply {
                addActionListener {
                    addLayer()
                }
            })
            add(JButton(Icons.trash).apply {
                addActionListener {
                    removeLayer()
                }
            })
        }, BorderLayout.PAGE_END)
    }

    fun addLayer() {
        listModel.insertRow(0, arrayOf("Layer ${listModel.rowCount}", true, false))
        list.setRowSelectionInterval(0, 0)

        Components.pixelGrid.layerList.add(0, PixelGrid.Layer())
    }

    fun removeLayer() {
        Components.pixelGrid.layerList.removeAt(list.selectedRow)
        listModel.removeRow(list.selectedRow)
    }
}