package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.event.reusable.EventCreateFile
import com.deflatedpickle.rawky.event.reusable.EventCreatePluginComponent
import com.deflatedpickle.rawky.event.reusable.EventDeserializedConfig
import com.deflatedpickle.rawky.event.reusable.EventDiscoverPlugin
import com.deflatedpickle.rawky.event.specific.EventDockDeployed
import com.deflatedpickle.rawky.event.reusable.EventLoadPlugin
import com.deflatedpickle.rawky.event.specific.EventLoadedPlugins
import com.deflatedpickle.rawky.event.specific.EventRawkyInit
import com.deflatedpickle.rawky.event.specific.EventSortedPluginLoadOrder
import com.deflatedpickle.rawky.event.specific.EventWindowShown
import com.deflatedpickle.rawky.ui.window.Window
import com.deflatedpickle.rawky.util.ClassGraphUtil
import com.deflatedpickle.rawky.util.ConfigUtil
import com.deflatedpickle.rawky.util.GeneralUtil
import com.deflatedpickle.rawky.util.PluginUtil
import org.apache.logging.log4j.LogManager
import org.oxbow.swingbits.dialog.task.TaskDialogs
import java.awt.Dimension
import java.io.File
import javax.swing.SwingUtilities
import javax.swing.UIManager
import kotlinx.serialization.ImplicitReflectionSerializer

@OptIn(ImplicitReflectionSerializer::class)
fun main(args: Array<String>) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    System.setProperty("log4j.skipJansi", "false")
    val logger = LogManager.getLogger("main")

    GeneralUtil.isInDev = args.contains("indev")

    logger.info("Running ${if (GeneralUtil.isInDev) "as source" else "as built"}")
    logger.warn(
        "Rawky is running with ${
        // This is in bytes, so we'll divide it by enough
        Runtime.getRuntime().maxMemory() / 1024 * 1024
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
    logger.info("Registered a default exception handler")

    // Plugins are distributed and loaded as JARs
    // when the program is built
    if (!GeneralUtil.isInDev) {
        EventCreateFile.trigger(
            PluginUtil.createPluginsFolder().apply {
                logger.info("Created the plugins folder at ${this.absolutePath}")
            }
        )
    }

    // Start a scan of the class graph
    // this will discover all plugins
    ClassGraphUtil.refresh()

    // Finds all singletons extending Plugin
    PluginUtil.discoverPlugins {
        EventDiscoverPlugin.trigger(it)
        // Validate all the small things
        PluginUtil.validateVersion(it) &&
                PluginUtil.validateDescription(it) &&
                PluginUtil.validateType(it) &&
                PluginUtil.validateDependencySlug(it) &&
                PluginUtil.validateDependencyExistence(it)
    }
    logger.debug("Validated all plugins with ${PluginUtil.unloadedPlugins.size} error/s")

    // Organise plugins by their dependencies
    PluginUtil.pluginLoadOrder.sortWith(Plugin.comparator)
    logger.info("Sorted out the load order: ${PluginUtil.pluginLoadOrder.map { PluginUtil.pluginToSlug(it) }}")
    EventSortedPluginLoadOrder.trigger(PluginUtil.pluginLoadOrder)

    // Loads all classes with a Plugin annotation
    for (i in PluginUtil.pluginLoadOrder) {
        PluginUtil.pluginMap[i]!!.loadClass().kotlin.objectInstance
        EventLoadPlugin.trigger(i)
    }
    logger.info("Loaded plugins; ${PluginUtil.pluginLoadOrder.dropWhile { it !in PluginUtil.unloadedPlugins }}")
    EventLoadedPlugins.trigger(PluginUtil.pluginLoadOrder)

    // Create the docked widgets
    for (plugin in PluginUtil.pluginLoadOrder) {
        for (component in plugin.components) {
            PluginUtil.createComponent(plugin, component)
            EventCreatePluginComponent.trigger(component.objectInstance!!)
        }
    }

    // Create the config file
    EventCreateFile.trigger(
        ConfigUtil.createConfigFolder().apply {
            logger.info("Created the config folder at ${this.absolutePath}")
        }
    )

    // Deserialize old configs
    val files = ConfigUtil.createConfigFolder().listFiles()

    if (files != null) {
        for (file in files) {
            ConfigUtil.deserializeConfig(file)

            EventDeserializedConfig.trigger(file)
            logger.info("Deserialized the config for $file from ${file.absolutePath}")
        }
    }

    // Create and serialize configs that don't exist
    for (plugin in PluginUtil.pluginLoadOrder) {
        val id = PluginUtil.pluginToSlug(plugin)

        if (!ConfigUtil.hasConfigFile(id)) {
            val file = File("config/$id.json")

            ConfigUtil.serializeConfig(id, file)
            logger.info("Serialized the config for ${PluginUtil.pluginToSlug(plugin)} to ${file.absolutePath}")
        }
    }

    EventRawkyInit.trigger(true)

    // Add a JVM hook to stop Discord RCP
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            logger.warn("The JVM instance running Rawky was shutdown")
            // Changes were probably made, let's serialize the configs again
            ConfigUtil.serializeAllConfigs()
            logger.info("Serialized all the configs")
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