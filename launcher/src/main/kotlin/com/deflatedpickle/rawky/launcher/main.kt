/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.api.plugin.DependencyComparator
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.event.*
import com.deflatedpickle.haruhi.util.*
import com.deflatedpickle.marvin.util.OSUtil
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.event.EventRegisterCellClass
import com.deflatedpickle.rawky.launcher.gui.Window
import com.deflatedpickle.rawky.pixelcell.collection.PixelCell
import com.deflatedpickle.rawky.tilecell.collection.TileCell
import com.jidesoft.plaf.LookAndFeelFactory
import java.awt.Dimension
import java.io.File
import javax.swing.SwingUtilities
import kotlin.system.exitProcess
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.serializer
import org.apache.logging.log4j.LogManager
import org.fusesource.jansi.AnsiConsole
import org.oxbow.swingbits.dialog.task.TaskDialogs
import org.oxbow.swingbits.util.Strings

@InternalSerializationApi
fun main(args: Array<String>) {
    // We'll count the startup time
    val startTime = System.nanoTime()

    // We set the LaF now so any error pop-ups use the use it
    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    // Setting this property gives us terminal colours
    System.setProperty("log4j.skipJansi", "false")
    val logger = LogManager.getLogger()

    logger.debug("Installed JANSI for this terminal session")
    AnsiConsole.systemInstall()

    // The gradle tasks pass in "indev" argument
    // if it doesn't exist it's not indev
    Haruhi.isInDev = args.contains("indev")

    logger.info(
        """
        |
        |OS  : ${OSUtil.getOS()} (${OSUtil.os})
        |Java: ${System.getProperty("java.version")} (${System.getProperty("java.vm.name")})
        |Dir : ${System.getProperty("user.dir")}
        |Dev?: ${Haruhi.isInDev}
    """.trimMargin()
    )

    Haruhi.json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true

        serializersModule = SerializersModule {
            polymorphic(Cell::class) {
                subclass(PixelCell::class)
                subclass(TileCell::class)
            }
        }
    }

    Haruhi.window = Window
    Haruhi.toastWindow = Window.toastWindow
    Haruhi.control = Window.control
    Haruhi.grid = Window.grid

    // Adds a single shutdown thread with an event
    // to reduce the instance count
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            logger.debug("Uninstalled JANSI for this terminal session")
            AnsiConsole.systemUninstall()

            logger.debug("The JVM instance running Quiver was shutdown")
            EventProgramShutdown.trigger(true)

            // Changes were probably made, let's serialize the configs again
            ConfigUtil.serializeAllConfigs()
            logger.info("Serialized all the configs")
        }
    })

    // Handle all uncaught exceptions to open a pop-up
    Thread.setDefaultUncaughtExceptionHandler { t, e ->
        logger.warn("${t.name} threw $e")

        File("error").apply {
            if (!this.exists()) {
                this.mkdirs()
            }

            e::class.simpleName?.let { name ->
                val currentMoment = Clock.System.now()
                val systemZone = currentMoment.toLocalDateTime(
                    TimeZone.currentSystemDefault()
                )

                File("error/$name-$systemZone.log").apply {
                    createNewFile()
                    writeText(Strings.stackStraceAsString(e))
                }
            }
        }

        // We'll invoke it on the Swing thread
        // This will wait at least for the window to open first
        SwingUtilities.invokeLater {
            // Open a dialog to report the error to the user
            TaskDialogs
                .build()
                .parent(Window)
                .showException(e)
            // If the window isn't open and an error pops up
            // it's probably not going to open
            if (!Window.isVisible) {
                exitProcess(0)
            }
        }
    }
    logger.debug("Registered a default exception handler")

    // Plugins are distributed and loaded as JARs
    // when the program is built
    if (!Haruhi.isInDev) {
        // EventCreateFile.trigger(
        PluginUtil.createPluginsFolder().apply {
            logger.info("Created the plugins folder at ${this.absolutePath}")
        }
        // )
    }

    // Create the config file
    // We do this whether it's built or not as they are always needed
    // EventCreateFile.trigger(
    ConfigUtil.createConfigFolder().apply {
        if (!this.exists()) {
            this.mkdir()
            logger.info("Created the config folder at ${this.absolutePath}")
        }
    }
    // )

    // Start a scan of the class graph
    // this will discover all plugins
    ClassGraphUtil.refresh()

    // Finds all singletons extending Plugin
    PluginUtil.discoverPlugins()
    logger.debug("Validated all plugins with ${PluginUtil.unloadedPlugins.size} error/s")

    // Organise plugins by their dependencies
    PluginUtil.discoveredPlugins.sortWith(
        DependencyComparator
            .thenComparing(Plugin::type)
            .thenComparing(Plugin::value)
    )
    logger.debug("Sorted out the load order: ${PluginUtil.discoveredPlugins.map { PluginUtil.pluginToSlug(it) }}")
    // EventSortedPluginLoadOrder.trigger(PluginUtil.discoveredPlugins)

    // Loads all classes with a Plugin annotation
    // But validate all the small things before loading
    PluginUtil.loadPlugins {
        // Versions must be semantic
        ValidateUtil.validateVersion(it) &&
                // Descriptions must contain a <br> tag
                ValidateUtil.validateDescription(it) &&
                // Specific types need a specified field
                ValidateUtil.validateType(it) &&
                // Dependencies should be "author@plugin#version"
                // PluginUtil.validateDependencySlug(it) &&
                // The dependency should exist
                ValidateUtil.validateDependencyExistence(it)
    }
    logger.info("Loaded plugins; ${PluginUtil.loadedPlugins.map { PluginUtil.pluginToSlug(it) }}")
    EventLoadedPlugins.trigger(PluginUtil.loadedPlugins)

    if (PluginUtil.unloadedPlugins.size > 0) {
        logger.warn("Failed to load; ${PluginUtil.unloadedPlugins.map { PluginUtil.pluginToSlug(it) }}")
    }

    val componentList = mutableListOf<PluginPanel>()
    for (plugin in PluginUtil.discoveredPlugins) {
        if (plugin.component != Nothing::class) {
            with(plugin.component.objectInstance!!) {
                DockUtil.createComponent(plugin, this)
                componentList.add(this)
                EventCreatePluginComponent.trigger(this)
            }
        }
    }
    EventCreatedPluginComponents.trigger(componentList)

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
    for (plugin in PluginUtil.loadedPlugins) {
        val id = PluginUtil.pluginToSlug(plugin)

        // Check if a plugin is supposed to have settings
        // then if it doesn't have a settings file
        if (plugin.settings != Nothing::class && !ConfigUtil.hasConfigFile(id)) {
            val file = File("config/$id.json")

            ConfigUtil.serializeConfig(id, file)
            logger.info("Serialized the config for ${PluginUtil.pluginToSlug(plugin)} to ${file.absolutePath}")
        }
    }

    val loadTime = System.nanoTime()
    logger.debug("Took ${((loadTime - startTime) / 1000) % 60} seconds to load")

    SwingUtilities.invokeLater {
        Window.size = Dimension(800, 600)
        Window.setLocationRelativeTo(null)

        Window.control.contentArea.deploy(Window.grid)

        // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        // SwingUtilities.updateComponentTreeUI(Window)
        LookAndFeelFactory.installJideExtension()

        // This is a catch-all event, used by plugins to run code that depends on setup
        // though the specific events could be used instead
        // For example, if a plugin needs access to a config, they could listen to this
        EventProgramFinishSetup.trigger(true)

        Window.isVisible = true

        val launchTime = System.nanoTime()
        logger.debug("Took ${((launchTime - loadTime) / 1000) % 60} seconds to launch")
    }
}
