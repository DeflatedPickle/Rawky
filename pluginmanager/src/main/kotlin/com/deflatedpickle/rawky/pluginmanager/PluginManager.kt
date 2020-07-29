package com.deflatedpickle.rawky.pluginmanager

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.event.reusable.EventMenuBuild
import com.deflatedpickle.rawky.event.specific.EventToastWindowShown
import com.deflatedpickle.rawky.ui.extension.addItem
import com.deflatedpickle.rawky.ui.menu.MenuTools
import com.deflatedpickle.rawky.ui.window.Window
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
            if (PluginUtil.loadedPlugins.isNotEmpty()) {
                Window.toastWindow.addToast(
                    ToastItem(
                        title = "Loaded Plugins",
                        content = PluginUtil.loadedPlugins.joinToString {
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
                        level = ToastLevel.WARNING,
                        title = "Unloaded Plugins",
                        content = PluginUtil.unloadedPlugins.joinToString {
                            it.value
                        }
                    )
                )
            }
        }
    }
}