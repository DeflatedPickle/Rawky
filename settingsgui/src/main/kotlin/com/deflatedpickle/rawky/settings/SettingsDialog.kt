package com.deflatedpickle.rawky.settings

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.extension.get
import com.deflatedpickle.rawky.extension.set
import com.deflatedpickle.rawky.ui.constraints.FillHorizontal
import com.deflatedpickle.rawky.ui.constraints.StickEast
import com.deflatedpickle.rawky.ui.constraints.StickWest
import com.deflatedpickle.rawky.ui.widget.ErrorLabel
import com.deflatedpickle.rawky.ui.widget.SearchList
import com.deflatedpickle.rawky.ui.window.Window
import com.deflatedpickle.rawky.util.ConfigUtil
import com.deflatedpickle.rawky.util.PluginUtil
import kotlinx.serialization.ImplicitReflectionSerializer
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.AlphaComposite
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GridBagConstraints
import java.awt.Insets
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
    val nodePlugin = DefaultMutableTreeNode("Plugins")

    val searchPanel = SearchList().apply {
        model.apply {
            insertNodeInto(nodePlugin, this.root as MutableTreeNode?, this.getChildCount(this.root))
        }

        tree.addTreeSelectionListener {
            if (tree.minSelectionRow == -1) return@addTreeSelectionListener

            val path = it.newLeadSelectionPath
            if (path != null) {
                SettingsPanel.removeAll()

                if (path.path.contains(nodePlugin)) {
                    val component = path.path.last()

                    val obj = (component as DefaultMutableTreeNode).userObject as String

                    if (PluginUtil.idToPlugin.containsKey(obj)) {
                        val plugin = PluginUtil.idToPlugin[obj]!!
                        if (plugin.settings != Nothing::class) {
                            for (i in plugin.settings.declaredMemberProperties) {
                                SettingsPanel.add(JLabel("${i.name}:"), StickEast)
                                SettingsPanel.add(JSeparator(JSeparator.HORIZONTAL), FillHorizontal)

                                SettingsPanel.add(
                                    when (i.returnType) {
                                        Boolean::class.createType() -> checkBox(plugin, i.name)
                                        else -> ErrorLabel("${i::class.simpleName} isn't supported yet!")
                                    } as Component,
                                    StickWest
                                )
                            }
                        }
                    }
                }

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
        val instance = ConfigUtil.getSettings<Any>(plugin.value)

        isSelected = instance.get(name)

        addActionListener {
            isSelected = !instance.get<Boolean>(name)
            instance.set(name, isSelected)

            ConfigUtil.serializeConfig(plugin.value)
        }
    }
}