package com.deflatedpickle.rawky.settings

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.event.EventMenuBuild
import com.deflatedpickle.rawky.event.EventRawkyInit
import com.deflatedpickle.rawky.ui.extension.addItem
import com.deflatedpickle.rawky.ui.menu.MenuFile
import com.deflatedpickle.rawky.util.PluginUtil
import java.util.regex.PatternSyntaxException
import javax.swing.tree.DefaultMutableTreeNode

@Plugin(
    value = "settings_gui",
    author = "DeflatedPickle",
    version = "1.0.0",
    dependencies = ["core", "discord_rpc"]
)
object SettingsGUI {
    init {
        EventMenuBuild.addListener {
            if (it is MenuFile) {
                it.addItem("Settings") {
                    SettingsDialog.isVisible = true
                }
            }
        }

        EventRawkyInit.addListener {
            for (plugin in PluginUtil.pluginLoadOrder) {
                if (plugin.settings != Nothing::class) {
                    SettingsDialog.searchPanel.model.insertNodeInto(
                        DefaultMutableTreeNode(plugin.value),
                        Categories.nodePlugin,
                        SettingsDialog.searchPanel.model.getChildCount(Categories.nodePlugin)
                    )
                }
            }
        }

        SettingsDialog.searchPanel.searchField.addActionListener {
            try {
                SettingsDialog.searchPanel.tree.searchable.search(it.actionCommand)
            } catch (e: PatternSyntaxException) {
            }
        }
    }
}