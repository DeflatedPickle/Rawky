package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.launcher.config.LauncherSettings
import com.deflatedpickle.rawky.launcher.gui.Toolbar
import com.deflatedpickle.rawky.util.ActionUtil
import com.deflatedpickle.undulation.extensions.add
import javax.swing.JMenu

@Plugin(
    value = "launcher",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A basic launcher
    """,
    type = PluginType.LAUNCHER,
    dependencies = [
        "deflatedpickle@core#1.0.0"
    ],
    settings = LauncherSettings::class
)
@Suppress("unused")
object Launcher {
    init {
        EventProgramFinishSetup.addListener {
            val menuBar = RegistryUtil.get(MenuCategory.MENU.name)
            (menuBar?.get(MenuCategory.FILE.name) as JMenu).apply {
                add("New", MonoIcon.FOLDER_NEW) { ActionUtil.newFile() }
                // add("Open", MonoIcon.FOLDER_OPEN) { ActionUtil.openPack() }
                addSeparator()
            }

            Toolbar.apply {
                add(icon = MonoIcon.FOLDER_NEW, tooltip = "New") { ActionUtil.newFile() }
                // add(icon = MonoIcon.FOLDER_OPEN, tooltip = "Open Pack") { ActionUtil.openPack() }
            }
        }
    }
}