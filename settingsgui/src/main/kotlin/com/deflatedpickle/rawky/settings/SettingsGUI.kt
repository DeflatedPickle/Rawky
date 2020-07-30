package com.deflatedpickle.rawky.settings

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.event.EventMenuBuild
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.ui.extension.addItem
import com.deflatedpickle.rawky.ui.menu.MenuFile
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

        EventProgramFinishSetup.addListener {
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