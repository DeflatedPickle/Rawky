package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.rawky.discord.DiscordRP
import com.deflatedpickle.rawky.event.EventWindowDeployed
import com.deflatedpickle.rawky.ui.component.Window
import com.deflatedpickle.rawky.util.ClassGraphUtil
import com.deflatedpickle.rawky.util.GeneralUtil
import com.deflatedpickle.rawky.util.PluginUtil
import net.arikia.dev.drpc.DiscordRPC
import net.arikia.dev.drpc.DiscordRichPresence
import org.apache.logging.log4j.LogManager
import java.awt.Dimension
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

    // Connect to Discord RCP
    DiscordRP.initializeRCP()
    DiscordRP.timer.start()

    // NOTE: This is triggered here as the launcher isn't a plugin
    DiscordRP.stack.push(
        DiscordRichPresence
            .Builder("Launcher")
            .setDetails("Hanging around, doing nothing")
            .setStartTimestamps(System.currentTimeMillis())
            .build()
    )

    // Add a JVM hook to stop Discord RCP
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            DiscordRP.shutdownRCP()
            DiscordRP.timer.stop()
        }
    })

    SwingUtilities.invokeLater {
        Window.deploy()
        EventWindowDeployed.trigger(Window)

        Window.size = Dimension(400, 400)
        Window.setLocationRelativeTo(null)

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        SwingUtilities.updateComponentTreeUI(Window)

        Window.isVisible = true
    }
}