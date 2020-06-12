package com.deflatedpickle.rawky.pluginmanager

import com.deflatedpickle.rawky.api.extension.removeAll
import com.deflatedpickle.rawky.component.Window
import com.deflatedpickle.rawky.util.PluginUtil
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXTable
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JSplitPane

object PluginManagerDialog : TaskDialog(Window, "Plugin Manager") {
    val tableModel = PluginManagerTableModel()
    val table = JXTable(this.tableModel)

    val panel = JXPanel()

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

        super.setVisible(visible)
    }
}