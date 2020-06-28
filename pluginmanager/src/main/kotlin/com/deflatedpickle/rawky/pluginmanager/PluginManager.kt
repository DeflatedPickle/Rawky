package com.deflatedpickle.rawky.pluginmanager

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.api.plugin.PluginType
import com.deflatedpickle.rawky.event.EventMenuBuild
import com.deflatedpickle.rawky.event.EventToastWindowShown
import com.deflatedpickle.rawky.extension.addItem
import com.deflatedpickle.rawky.ui.menu.MenuTools
import com.deflatedpickle.rawky.ui.window.Window
import com.deflatedpickle.rawky.util.PluginUtil
import com.deflatedpickle.tosuto.ToastItem
import com.deflatedpickle.tosuto.action.ToastSingleAction

@Plugin(
    value = "plugin_manager",
    author = "DeflatedPickle",
    description = """
        <br>
        A GUI for managing plugins
    """,
    type = PluginType.DIALOG,
    dependencies = ["all"]
)
@Suppress("unused")
object PluginManager {
    init {
        EventMenuBuild.addListener {
            if (it is MenuTools) {
                it.addItem("Plugin Manager") {
                    PluginManagerDialog.isVisible = true
                }
            }
        }

        EventToastWindowShown.addListener {
            if (PluginUtil.pluginLoadOrder.isNotEmpty()) {
                Window.toastWindow.addToast(
                    ToastItem(
                        title = "Loaded Plugins",
                        content = PluginUtil.pluginLoadOrder.joinToString {
                            it.value
                        },
                        actions = listOf(
                            ToastSingleAction(
                                "Manage",
                                command = { _, _ ->
                                    PluginManagerDialog.isVisible = true
                                }
                            )
                        )
                    )
                )
            }
        }
    }
}