@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.goofy.oneko

import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.rawky.launcher.gui.Window
import com.deflatedpickle.undulation.functions.extensions.add
import oneko.Neko
import javax.swing.JMenu

@Plugin(
    value = "oneko",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Spawn cats that chase your mouse
    """,
    type = PluginType.OTHER,
)
object ONekoPlugin {
    init {
        EventProgramFinishSetup.addListener {
            RegistryUtil.get(MenuCategory.MENU.name)?.apply {
                (get(MenuCategory.TOOLS.name) as JMenu).apply {
                    add("Spawn Cat") {
                        Neko(Window)
                    }

                    addSeparator()
                }
            }
        }
    }
}