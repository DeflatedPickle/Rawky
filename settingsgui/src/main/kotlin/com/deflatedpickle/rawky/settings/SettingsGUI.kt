package com.deflatedpickle.rawky.settings

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.event.reusable.EventMenuBuild
import com.deflatedpickle.rawky.event.specific.EventRawkyInit
import com.deflatedpickle.rawky.ui.extension.addItem
import com.deflatedpickle.rawky.ui.menu.MenuFile
import com.deflatedpickle.rawky.util.PluginUtil
import java.util.regex.PatternSyntaxException
import javax.swing.tree.DefaultMutableTreeNode

@Plugin(
    value = "settings_gui",
    author = "DeflatedPickle",
    version = "1.0.0",
    dependencies = [
        "deflatedpickle@core#1.0.0",
        "deflatedpickle@discord_rpc#1.0.0"
    ]
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
            for (plugin in PluginUtil.discoveredPlugins) {
                if (plugin.settings != Nothing::class) {
                    SettingsDialog.searchPanel.model.insertNodeInto(
                        DefaultMutableTreeNode(PluginUtil.pluginToSlug(plugin)),
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