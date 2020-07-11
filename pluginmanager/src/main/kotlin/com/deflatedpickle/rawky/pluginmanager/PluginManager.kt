package com.deflatedpickle.rawky.pluginmanager

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.api.plugin.PluginType
import com.deflatedpickle.rawky.event.EventMenuBuild
import com.deflatedpickle.rawky.event.EventToastWindowShown
import com.deflatedpickle.rawky.ui.extension.addItem
import com.deflatedpickle.rawky.ui.menu.MenuTools
import com.deflatedpickle.rawky.ui.window.Window
import com.deflatedpickle.rawky.util.PluginUtil
import com.deflatedpickle.tosuto.ToastItem
import com.deflatedpickle.tosuto.action.ToastSingleAction
import com.deflatedpickle.tosuto.api.ToastLevel

@Plugin(
    value = "plugin_manager",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A GUI for managing plugins
    """,
    type = PluginType.DIALOG
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
                            PluginUtil.pluginToSlug(it)
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

            if (PluginUtil.unloadedPlugins.isNotEmpty()) {
                Window.toastWindow.addToast(
                    ToastItem(
                        level = ToastLevel.ERROR,
                        title = "Invalid Plugins",
                        content = PluginUtil.unloadedPlugins.joinToString {
                            it.value
                        }
                    )
                )
            }
        }
    }
}