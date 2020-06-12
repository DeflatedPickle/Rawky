package com.deflatedpickle.rawky

import com.deflatedpickle.rawky.api.util.ClassGraphUtil
import com.deflatedpickle.rawky.api.util.PluginUtil
import com.deflatedpickle.rawky.component.Window
import java.awt.Dimension
import javax.swing.SwingUtilities

fun main() {
    // Start a scan of the class graph
    // This will discover all plugins
    ClassGraphUtil.refresh()
    // Finds all singletons extending Plugin
    PluginUtil.discoverPlugins()
    PluginUtil.createComponents()

    Window.size = Dimension(400, 400)
    Window.isVisible = true

    SwingUtilities.invokeLater {
        Window.deploy()
    }
}