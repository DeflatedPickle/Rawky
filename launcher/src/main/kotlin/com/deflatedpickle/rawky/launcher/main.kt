package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.api.plugin.PluginType
import com.deflatedpickle.rawky.event.reusable.EventCreateFile
import com.deflatedpickle.rawky.event.reusable.EventCreatePluginComponent
import com.deflatedpickle.rawky.event.reusable.EventDeserializedConfig
import com.deflatedpickle.rawky.event.specific.EventDockDeployed
import com.deflatedpickle.rawky.event.specific.EventLoadedPlugins
import com.deflatedpickle.rawky.event.specific.EventRawkyInit
import com.deflatedpickle.rawky.event.specific.EventSortedPluginLoadOrder
import com.deflatedpickle.rawky.event.specific.EventWindowShown
import com.deflatedpickle.rawky.launcher.config.LauncherSettings
import com.deflatedpickle.rawky.event.specific.EventRawkyShutdown
import com.deflatedpickle.rawky.ui.menu.MenuBar
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
import kotlin.reflect.full.createInstance

@OptIn(ImplicitReflectionSerializer::class)
fun main(args: Array<String>) {
    // We set the LaF now so any error pop-ups use the use it
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    // Setting this property gives us terminal colours
    System.setProperty("log4j.skipJansi", "false")
    val logger = LogManager.getLogger("main")

    // The gradle tasks pass in "indev" argument
    // if it doesn't exist it's not indev
    GeneralUtil.isInDev = args.contains("indev")

    logger.info("Running ${if (GeneralUtil.isInDev) "as source" else "as built"}")
    logger.warn(
        "Rawky is running with ${
        // This is in bytes, so we'll divide it by enough
        Runtime.getRuntime().maxMemory() / 1024 * 1024
        }MBs of memory"
    )

    // Adds a single shutdown thread with an event
    // to reduce the instance count
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            logger.warn("The JVM instance running Rawky was shutdown")
            EventRawkyShutdown.trigger(true)
            // Changes were probably made, let's serialize the configs again
            ConfigUtil.serializeAllConfigs()
            logger.info("Serialized all the configs")
        }
    })

    // Handle all uncaught exceptions to open a pop-up
    // imagine catching every type of error everywhere just to open a pop-up
    // this comment was made by no bloat gang
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

    // Serialize/deserialize a config for the core
    // This can't use the plugin config system as it
    // can dictate what plugins are/aren't loaded
    val launcherID = "deflatedpickle@launcher#1.0.0"
    val launcherSettingsFile = File("config/$launcherID.json")
    var launcherSettingsInstance = LauncherSettings::class.createInstance()

    if (!ConfigUtil.hasConfigFile(launcherID)) {
        ConfigUtil.serializeConfigToInstance(launcherSettingsFile, launcherSettingsInstance)
    } else {
        launcherSettingsInstance = ConfigUtil.deserializeConfigToInstance(
            launcherSettingsFile, launcherSettingsInstance
        ) as LauncherSettings
    }

    // Start a scan of the class graph
    // this will discover all plugins
    ClassGraphUtil.refresh()

    // Finds all singletons extending Plugin
    PluginUtil.discoverPlugins()
    logger.debug("Validated all plugins with ${PluginUtil.unloadedPlugins.size} error/s")

    // Organise plugins by their dependencies
    PluginUtil.discoveredPlugins.sortWith(Plugin.comparator)
    logger.info("Sorted out the load order: ${PluginUtil.discoveredPlugins.map { PluginUtil.pluginToSlug(it) }}")
    EventSortedPluginLoadOrder.trigger(PluginUtil.discoveredPlugins)

    // Loads all classes with a Plugin annotation
    PluginUtil.loadPlugins {
        val slug = PluginUtil.pluginToSlug(it)
        // Validate all the small things

        // Versions must be semantic
        PluginUtil.validateVersion(it) &&
                // Descriptions must contain a <br> tag
                PluginUtil.validateDescription(it) &&
                // Specific types need a specified field
                PluginUtil.validateType(it) &&
                // Dependencies should be "author@plugin#version"
                PluginUtil.validateDependencySlug(it) &&
                // The dependency should exist
                PluginUtil.validateDependencyExistence(it) &&
                // Ask if the user wants to enable it
                // Just to make sure they know what they're loading
                // They might've got the plugin set from elsewhere
                (
                        // Ignore facade types
                        (it.type in arrayOf(
                            PluginType.CORE_API,
                            PluginType.LAUNCHER
                        )) ||
                                // Check it's not already saved to be enabled
                                !launcherSettingsInstance.enabledPlugins
                                    .contains(PluginUtil.pluginToSlug(it)) &&
                                // Open a dialog to ask the user
                                // TODO: Make a custom dialog to show the user what classes, components and configs a plugin adds before they enable it
                                TaskDialogs.ask(
                                    Window,
                                    "",
                                    "Should $slug be activated?"
                                ) || launcherSettingsInstance.enabledPlugins
                            .contains(slug))
    }
    logger.info("Loaded plugins; ${PluginUtil.loadedPlugins.map { PluginUtil.pluginToSlug(it) }}")
    EventLoadedPlugins.trigger(PluginUtil.loadedPlugins)

    // Create the docked widgets
    for (plugin in PluginUtil.discoveredPlugins) {
        for (component in plugin.components) {
            PluginUtil.createComponent(plugin, component)
            EventCreatePluginComponent.trigger(component.objectInstance!!)
        }
    }

    // Add newly enabled plugins to the core settings
    for (plug in PluginUtil.discoveredPlugins) {
        val slug = PluginUtil.pluginToSlug(plug)

        if (!launcherSettingsInstance.enabledPlugins.contains(slug)) {
            launcherSettingsInstance.enabledPlugins.add(slug)
        }
    }
    // Serialize the enabled plugins
    ConfigUtil.serializeConfigToInstance(
        launcherSettingsFile, launcherSettingsInstance
    )

    // Create the config file
    EventCreateFile.trigger(
        ConfigUtil.createConfigFolder().apply {
            if (!this.exists()) {
                logger.info("Created the config folder at ${this.absolutePath}")
            }
        }
    )

    // Deserialize old configs
    val files = ConfigUtil.createConfigFolder().listFiles()

    if (files != null) {
        for (file in files) {
            if (ConfigUtil.deserializeConfig(file)) {
                EventDeserializedConfig.trigger(file)
                logger.info("Deserialized the config for $file from ${file.absolutePath}")
            }
        }
    }

    // Create and serialize configs that don't exist
    for (plugin in PluginUtil.discoveredPlugins) {
        val id = PluginUtil.pluginToSlug(plugin)

        // Check if a plugin is supposed to have settings
        // then if it doesn't have a settings file
        if (plugin.settings != Nothing::class && !ConfigUtil.hasConfigFile(id)) {
            val file = File("config/$id.json")

            ConfigUtil.serializeConfig(id, file)
            logger.info("Serialized the config for ${PluginUtil.pluginToSlug(plugin)} to ${file.absolutePath}")
        }
    }

    // This is a catch-all event, used by plugins to run code that depends on setup
    // though the specific events could be used instead
    // For example, if a plugin needs access to a config, they could listen to this
    EventRawkyInit.trigger(true)

    SwingUtilities.invokeLater {
        Window.deploy()
        EventDockDeployed.trigger(Window.grid)

        Window.jMenuBar = MenuBar
        Window.size = Dimension(400, 400)
        Window.setLocationRelativeTo(null)

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        SwingUtilities.updateComponentTreeUI(Window)
        SwingUtilities.updateComponentTreeUI(Window.toastWindow)

        Window.isVisible = true
        EventWindowShown.trigger(Window)
    }
}