package com.deflatedpickle.rawky.pluginmanager

import com.deflatedpickle.rawky.api.extension.removeAll
import com.deflatedpickle.rawky.component.Window
import com.deflatedpickle.rawky.util.PluginUtil
import org.jdesktop.swingx.JXTable
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Dimension
import javax.swing.*

object PluginManagerDialog : TaskDialog(Window, "Plugin Manager") {
    val tableModel = PluginManagerTableModel()
    val table = JXTable(this.tableModel).apply {
        val table = this
        autoResizeMode = JTable.AUTO_RESIZE_OFF
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        isEditable = false

        selectionModel.addListSelectionListener {
            if (!it.valueIsAdjusting) {
                this@PluginManagerDialog.panel.header.apply {
                    this.nameVersion.apply {
                        this.nameLabel.text =
                            PluginUtil.pluginLoadOrder[table.selectedRow]
                                .value
                                .split("_")
                                .joinToString(" ") { it.capitalize() }
                        this.versionLabel.text = PluginUtil.pluginLoadOrder[table.selectedRow].version
                    }

                    this.authorLabel.text = PluginUtil.pluginLoadOrder[table.selectedRow].author
                    this.descriptionPanel.descriptionLabel.text =
                        "<html>${
                        PluginUtil
                            .pluginLoadOrder[table.selectedRow]
                            .description.split("<br>").drop(1)[0]
                            .replace("<br>", "<br><br>")
                            .trimIndent()
                        }</html>"
                }
            }
        }
    }

    private val panel = PluginManagerPanel()

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = JPanel().apply {
            isOpaque = false
            layout = BoxLayout(this, BoxLayout.Y_AXIS)

            preferredSize = Dimension(400, 200)

            add(
                JSplitPane(
                    JSplitPane.HORIZONTAL_SPLIT,
                    this@PluginManagerDialog.table,
                    this@PluginManagerDialog.panel
                )
            )
        }
    }

    override fun setVisible(visible: Boolean) {
        this.tableModel.removeAll()
        for (plug in PluginUtil.pluginLoadOrder) {
            this.tableModel.addRow(arrayOf(plug.value, plug.author, plug.version))
        }

        this.table.setRowSelectionInterval(0, 0)

        super.setVisible(visible)
    }
}