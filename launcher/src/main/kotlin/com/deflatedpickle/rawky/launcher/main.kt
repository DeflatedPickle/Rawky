package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.rawky.function.umbrella
import com.deflatedpickle.rawky.util.ClassGraphUtil
import com.deflatedpickle.rawky.util.PluginUtil
import com.deflatedpickle.rawky.ui.component.Window
import com.deflatedpickle.rawky.util.GeneralUtil
import org.apache.logging.log4j.LogManager
import java.awt.Dimension
import java.lang.IllegalArgumentException
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main(args: Array<String>) {
    System.setProperty("log4j.skipJansi", "false")
    val logger = LogManager.getLogger("main")

    GeneralUtil.isInDev = args.contains("indev")

    logger.info("Running ${if (GeneralUtil.isInDev) "as source" else "as built"}")

    // Plugins are distributed and loaded as JARs
    // When the program is built
    if (!GeneralUtil.isInDev) {
        PluginUtil.createPluginsFolder()
    }
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

        Window.size = Dimension(400, 400)
        Window.setLocationRelativeTo(null)

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        SwingUtilities.updateComponentTreeUI(Window)

        Window.isVisible = true
    }
}