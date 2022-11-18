package com.deflatedpickle.rawky.layerlist

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.dialog.NewLayerDialog
import com.deflatedpickle.rawky.event.EventChangeLayer
import com.deflatedpickle.rawky.event.EventNewLayer
import com.deflatedpickle.rawky.event.EventUpdateCell
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.rawky.event.packet.PacketChange
import com.deflatedpickle.rawky.pixelgrid.setting.PixelGridSettings
import com.deflatedpickle.rawky.util.DrawUtil
import com.deflatedpickle.undulation.functions.extensions.add
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.BasicStroke
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.util.*
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

object LayerListPanel : PluginPanel() {
    private val toolbar = JToolBar("Layer list").apply {
        add(icon = MonoIcon.ADD_ELEMENT, tooltip = "Add element") {
            RawkyPlugin.document?.let { doc ->
                val layerDialog = NewLayerDialog()
                layerDialog.isVisible = true

                if (layerDialog.result == TaskDialog.StandardCommand.OK) {

                    val layer = doc.children[doc.selectedIndex].addLayer(
                        layerDialog.nameInput.text,
                        layerDialog.columnInput.value as Int,
                        layerDialog.rowInput.value as Int,
                        // layerDialog.indexInput.value as Int
                    )

                    model.insertRow(
                        0, arrayOf(
                            null,
                            layerDialog.nameInput.text,
                            true,
                            false
                        )
                    )
                    table.setRowSelectionInterval(0, 0)

                    EventNewLayer.trigger(layer)
                }
            }
        }
        add(icon = MonoIcon.EDIT_ELEMENT, tooltip = "Edit element", enabled = false) {

        }
        add(icon = MonoIcon.DELETE_ELEMENT, tooltip = "Delete element", enabled = false) {

        }
        add(icon = MonoIcon.DELETE_ALL_ELEMENTS, tooltip = "Delete all elements", enabled = false) {

        }
    }

    private val navbar = JToolBar("Navbar").apply {
        orientation = JToolBar.VERTICAL

        add(icon = MonoIcon.ARROW_UP, tooltip = "Decrement layer") {
            RawkyPlugin.document?.let { doc ->
                val frame = doc.children[doc.selectedIndex]

                if (frame.selectedIndex - 1 >= 0) {
                    table.setRowSelectionInterval(frame.selectedIndex - 1, frame.selectedIndex - 1)
                }
            }
        }

        add(icon = MonoIcon.ARROW_DOWN, tooltip = "Increment layer") {
            RawkyPlugin.document?.let { doc ->
                val frame = doc.children[doc.selectedIndex]

                if (frame.selectedIndex + 1 < frame.children.size) {
                    table.setRowSelectionInterval(frame.selectedIndex + 1, frame.selectedIndex + 1)
                }
            }
        }
    }

    val model = DefaultTableModel(arrayOf(), arrayOf("Preview", "Name", "Visibility", "Lock")).apply {
        addTableModelListener {
            RawkyPlugin.document?.let { doc ->
                when (it.column) {
                    2 -> doc.children[doc.selectedIndex].children[it.firstRow].visible =
                        this.getValueAt(it.firstRow, it.column) as Boolean

                    3 -> doc.children[doc.selectedIndex].children[it.firstRow].lock =
                        this.getValueAt(it.firstRow, it.column) as Boolean
                }

                EventUpdateGrid.trigger(doc.children[doc.selectedIndex].children[it.firstRow].child)
            }
        }
    }

    private val columnZeroRenderer = TableCellRenderer { _: JTable, _: Any?,
                                                         _: Boolean, _: Boolean,
                                                         row: Int, _: Int ->
        object : JPanel() {
            override fun paintComponent(g: Graphics) {
                if (RawkyPlugin.document == null) return

                val g2D = g as Graphics2D

                // TODO: Scale based on the grid size
                g2D.scale(0.14, 0.14)

                ConfigUtil.getSettings<PixelGridSettings>("deflatedpickle@pixel_grid#*")?.let { settings ->
                    RawkyPlugin.document?.let {
                        val layer = it.children[it.selectedIndex].children[row]

                        DrawUtil.paintGrid(
                            g, layer.child, settings.divide.colour,
                            BasicStroke(settings.divide.thickness)
                        )
                    }
                }
            }
        }
    }
    private val columnOneEditor: TableCellEditor = object : AbstractCellEditor(), TableCellEditor {
        override fun isCellEditable(e: EventObject) = false
        override fun getTableCellEditorComponent(
            table: JTable,
            value: Any?,
            isSelected: Boolean,
            row: Int, column: Int
        ) = JPanel() as Component

        override fun getCellEditorValue() = 0
    }
    private val columnTwoEditor = DefaultCellEditor(JCheckBox().apply {
        isVisible = false
    })
    private val columnTwoRenderer = TableCellRenderer { _, value, _, _, _, _ ->
        JCheckBox(if (value as Boolean) MonoIcon.SHOW else MonoIcon.HIDE)
            .apply { isOpaque = false }
    }
    private val columnThreeEditor = DefaultCellEditor(JCheckBox().apply {
        isVisible = false
    })
    private val columnThreeRenderer = TableCellRenderer { _, value, _, _, _, _ ->
        JCheckBox(if (value as Boolean) MonoIcon.LOCK_OPEN else MonoIcon.LOCK_CLOSED)
            .apply { isOpaque = false }
    }

    val table = JTable(model).apply {
        showVerticalLines = false
        tableHeader = null
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)

        rowHeight = 40

        selectionModel.addListSelectionListener {
            if (!it.valueIsAdjusting) {
                RawkyPlugin.document?.let { doc ->
                    val frame = doc.children[doc.selectedIndex]

                    val oldLayer = frame.children[frame.selectedIndex]
                    frame.selectedIndex = min(frame.children.size - 1, selectionModel.anchorSelectionIndex)

                    val newLayer = frame.children[frame.selectedIndex]
                    val grid = newLayer.child

                    EventUpdateGrid.trigger(grid)
                    EventChangeLayer.trigger(
                        PacketChange(
                            System.nanoTime(),
                            PluginUtil.slugToPlugin("deflatedpickle@layer_list")!!,
                            oldLayer,
                            newLayer,
                        )
                    )
                }
            }
        }

        columnModel.apply {
            getColumn(0).apply {
                maxWidth = 40

                cellRenderer = columnZeroRenderer
            }

            getColumn(1).apply {
                cellEditor = columnOneEditor
            }

            columnModel.getColumn(2).apply {
                maxWidth = 30

                cellEditor = columnTwoEditor
                cellRenderer = columnTwoRenderer
            }

            columnModel.getColumn(3).apply {
                maxWidth = 30

                cellEditor = columnThreeEditor
                cellRenderer = columnThreeRenderer
            }
        }
    }

    init {
        layout = BorderLayout()

        EventUpdateGrid.addListener {
            table.repaint()
        }

        EventUpdateCell.addListener {
            table.repaint()
        }

        add(toolbar, BorderLayout.SOUTH)
        add(navbar, BorderLayout.EAST)
        add(JScrollPane(table), BorderLayout.CENTER)
    }
}