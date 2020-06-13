package com.deflatedpickle.rawky.pluginmanager

import com.deflatedpickle.rawky.extension.addItem
import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.event.EventMenuBuild
import com.deflatedpickle.rawky.menu.MenuTools

@Plugin(
    value = "plugin_manager",
    author = "DeflatedPickle",
    description = """
        <br>
        A GUI for managing plugins
    """,
    dependencies = ["all"]
)
object PluginManager {
    init {
        EventMenuBuild.addListener {
            if (it is MenuTools) {
                it.addItem("Plugin Manager") {
                    PluginManagerDialog.isVisible = true
                }
            }
        }
    }
}