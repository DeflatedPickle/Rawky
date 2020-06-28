package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.rawky.discord.DiscordRP
import com.deflatedpickle.rawky.event.EventDockDeployed
import com.deflatedpickle.rawky.event.EventWindowShown
import com.deflatedpickle.rawky.ui.window.Window
import com.deflatedpickle.rawky.util.ClassGraphUtil
import com.deflatedpickle.rawky.util.GeneralUtil
import com.deflatedpickle.rawky.util.PluginUtil
import net.arikia.dev.drpc.DiscordRichPresence
import org.apache.logging.log4j.LogManager
import org.oxbow.swingbits.dialog.task.TaskDialog
import org.oxbow.swingbits.dialog.task.TaskDialogs
import java.awt.Dimension
import javax.swing.JDialog
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main(args: Array<String>) {
    System.setProperty("log4j.skipJansi", "false")
    val logger = LogManager.getLogger("main")

    GeneralUtil.isInDev = args.contains("indev")

    logger.info("Running ${if (GeneralUtil.isInDev) "as source" else "as built"}")
    logger.warn(
        "Rawky is running with ${
        // This is in bytes, so we'll divide it by enough
        Runtime.getRuntime().maxMemory() / 1_000_000
        }MBs of memory"
    )

    Thread.setDefaultUncaughtExceptionHandler { t, e ->
        logger.warn("${t.name} threw $e")
        // We'll invoke it on the Swing thread
        // This will wait at least for the window to open first
        SwingUtilities.invokeLater {
            // Open a dialog to report the error to the user
            TaskDialogs
                .build()
                .parent(Window)
                .showException(e)
        }
    }
    logger.info("Registered default exception handler")

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
            logger.warn("The JVM instance running Rawky was shutdown")
            DiscordRP.shutdownRCP()
            DiscordRP.timer.stop()
        }
    })

    SwingUtilities.invokeLater {
        Window.deploy()
        EventDockDeployed.trigger(Window.grid)

        Window.size = Dimension(400, 400)
        Window.setLocationRelativeTo(null)

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        SwingUtilities.updateComponentTreeUI(Window)
        SwingUtilities.updateComponentTreeUI(Window.toastWindow)

        Window.isVisible = true
        EventWindowShown.trigger(Window)
    }
}