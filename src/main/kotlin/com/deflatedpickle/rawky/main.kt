package com.deflatedpickle.rawky

import com.deflatedpickle.rawky.util.ClassGraphUtil
import com.deflatedpickle.rawky.util.PluginUtil
import com.deflatedpickle.rawky.component.Window
import com.deflatedpickle.rawky.event.EventMenuBarBuild
import java.awt.Dimension
import javax.swing.SwingUtilities

fun main() {
    System.setProperty("log4j.skipJansi", "false")
    // Start a scan of the class graph
    // This will discover all plugins
    ClassGraphUtil.refresh()
    // Finds all singletons extending Plugin
    PluginUtil.discoverPlugins()
    // Warns if dependencies can't be found
    PluginUtil.validateDependencies()
    // Organise plugins by their dependencies
    PluginUtil.figureOutLoadOrder()
    // Loads all classes with a Plugin annotation
    PluginUtil.loadPlugins()
    // Create the docked widgets
    PluginUtil.createComponents()

    SwingUtilities.invokeLater {
        Window.deploy()
    }

    Window.size = Dimension(400, 400)
    Window.isVisible = true
}