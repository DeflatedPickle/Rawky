package com.deflatedpickle.rawky.settings

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.extension.get
import com.deflatedpickle.rawky.extension.set
import com.deflatedpickle.rawky.ui.constraints.FillBothFinishLine
import com.deflatedpickle.rawky.ui.constraints.FillHorizontal
import com.deflatedpickle.rawky.ui.constraints.StickEast
import com.deflatedpickle.rawky.ui.constraints.StickWestFinishLine
import com.deflatedpickle.rawky.ui.widget.ErrorLabel
import com.deflatedpickle.rawky.ui.widget.SearchList
import com.deflatedpickle.rawky.ui.window.Window
import com.deflatedpickle.rawky.util.ConfigUtil
import kotlinx.serialization.ImplicitReflectionSerializer
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Component
import java.awt.Dimension
import java.io.File
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.JSplitPane
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.MutableTreeNode
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties

@OptIn(ImplicitReflectionSerializer::class)
object SettingsDialog : TaskDialog(Window, "Settings") {
    private val paddingPanel = JPanel().apply {
        val dimension = Dimension(1, 1)

        preferredSize = dimension
        minimumSize = dimension
    }

    val searchPanel = SearchList().apply {
        model.apply {
            insertNodeInto(
                Categories.nodePlugin,
                this.root as MutableTreeNode?,
                this.getChildCount(this.root)
            )
        }

        tree.addTreeSelectionListener {
            if (tree.minSelectionRow == -1) return@addTreeSelectionListener

            val path = it.newLeadSelectionPath
            if (path != null) {
                SettingsPanel.removeAll()

                if (path.path.contains(Categories.nodePlugin)) {
                    val component = path.path.last()

                    val obj = (component as DefaultMutableTreeNode).userObject as String

                    if (PluginUtil.slugToPlugin.containsKey(obj)) {
                        val plugin = PluginUtil.slugToPlugin[obj]!!
                        if (plugin.settings != Nothing::class) {
                            for (i in plugin.settings.declaredMemberProperties) {
                                SettingsPanel.add(JLabel("${i.name}:"), StickEast)
                                SettingsPanel.add(JSeparator(JSeparator.HORIZONTAL), FillHorizontal)

                                SettingsPanel.add(
                                    when (i.returnType) {
                                        Boolean::class.createType() -> checkBox(plugin, i.name)
                                        else -> ErrorLabel("${i::class.simpleName} isn't supported yet!")
                                    } as Component,
                                    StickWestFinishLine
                                )
                            }
                        }
                    }
                }

                SettingsPanel.add(this@SettingsDialog.paddingPanel, FillBothFinishLine)

                SettingsPanel.doLayout()
                SettingsPanel.repaint()
                SettingsPanel.revalidate()
            }
        }
    }

    private val splitPane = JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        searchPanel, SettingsPanel
    ).apply {
        isOneTouchExpandable = true
    }

    init {
        setCommands(StandardCommand.OK)

        this.fixedComponent = JPanel().apply {
            isOpaque = false
            layout = BoxLayout(this, BoxLayout.Y_AXIS)

            preferredSize = Dimension(400, 200)

            add(this@SettingsDialog.splitPane)
        }
    }

    private fun checkBox(plugin: Plugin, name: String): JCheckBox = JCheckBox().apply {
        val instance = ConfigUtil.getSettings<Any>(PluginUtil.pluginToSlug(plugin))

        isSelected = instance.get(name)

        addActionListener {
            isSelected = !instance.get<Boolean>(name)
            instance.set(name, isSelected)

            val id = PluginUtil.pluginToSlug(plugin)
            ConfigUtil.serializeConfig(
                id, File("config/$id.json")
            )
        }
    }
}