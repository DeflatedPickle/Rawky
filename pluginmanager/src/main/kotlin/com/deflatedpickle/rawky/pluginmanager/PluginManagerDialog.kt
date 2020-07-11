package com.deflatedpickle.rawky.pluginmanager

import com.deflatedpickle.rawky.discordrpc.util.DiscordRP
import com.deflatedpickle.rawky.ui.window.Window
import com.deflatedpickle.rawky.util.PluginUtil
import net.arikia.dev.drpc.DiscordRichPresence
import org.jdesktop.swingx.JXTree
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode
import org.jdesktop.swingx.treetable.MutableTreeTableNode
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.BoxLayout
import javax.swing.JDialog
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultMutableTreeNode

object PluginManagerDialog : TaskDialog(Window, "Plugin Manager") {
    private val panel = PluginManagerPanel()

    private val treeRootNode = DefaultMutableTreeNode("plugin")
    private val tree = JXTree(treeRootNode).apply {
        val table = this
        isEditable = false

        addTreeSelectionListener {
            if (table.minSelectionRow == -1) return@addTreeSelectionListener

            val selected = PluginUtil.pluginLoadOrder[table.minSelectionRow]
            val dependencies = selected.dependencies

            panel.dependencies.dependenciesTableTree.treeTableModel =
                PluginManagerTreeTableModel(
                    dependencies.map {
                        PluginUtil.slugToPlugin[it]!!
                    }.toTypedArray()
                )

            this@PluginManagerDialog.panel.header.apply {
                this.nameLabel.text =
                    PluginUtil.pluginLoadOrder[table.minSelectionRow]
                        .value
                        .split("_")
                        .joinToString(" ") { it.capitalize() }
                this.versionLabel.text = "v${
                PluginUtil.pluginLoadOrder[table.minSelectionRow].version
                }"

                this.authorLabel.text = "By ${
                PluginUtil.pluginLoadOrder[table.minSelectionRow].author
                }"

                this.typeLabel.text = "Type: ${
                PluginUtil.pluginLoadOrder[table.minSelectionRow].type.name
                }"

                this.descriptionLabel.text =
                    "<html>${
                    PluginUtil
                        .pluginLoadOrder[table.minSelectionRow]
                        // Split it, get rid of the short description
                        .description.split("<br>").drop(1)[0]
                        // One BR is too small for me, need b i g
                        .replace("<br>", "<br><br>")
                        .trimIndent()
                    }</html>"
            }

            this@PluginManagerDialog.panel.dependencies.apply {
                this.dependenciesTableTree.removeAll()
                val dependencies = PluginUtil.pluginLoadOrder[table.minSelectionRow].dependencies

                if (dependencies.isEmpty()) {
                    (panel.dependencies.dependenciesTableTree.treeTableModel.root as MutableTreeTableNode).insert(
                        DefaultMutableTreeTableNode("none"),
                        0
                    )
                } else {
                    for (i in dependencies) {
                        (panel.dependencies.dependenciesTableTree.treeTableModel.root as MutableTreeTableNode).insert(
                            DefaultMutableTreeTableNode(i),
                            0
                        )
                    }
                }
            }
        }
    }

    private val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tree, panel).apply {
        isOneTouchExpandable = true
    }

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.tree.isRootVisible = false

        this.fixedComponent = JPanel().apply {
            isOpaque = false
            layout = BoxLayout(this, BoxLayout.Y_AXIS)

            preferredSize = Dimension(400, 200)

            add(this@PluginManagerDialog.splitPane)
        }

        (SwingUtilities.getWindowAncestor(this.fixedComponent) as JDialog).addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                DiscordRP.stack.pop()
            }
        })
    }

    override fun setVisible(visible: Boolean) {
        for (plug in PluginUtil.pluginLoadOrder) {
            this.treeRootNode.add(DefaultMutableTreeNode(plug.value))
        }

        this.tree.setSelectionRow(0)
        this.tree.expandAll()

        DiscordRP.stack.push(
            DiscordRichPresence
                .Builder("Plugin Manager")
                .setDetails("Managing plugins")
                .setStartTimestamps(System.currentTimeMillis())
                .build()
        )

        super.setVisible(visible)
    }
}