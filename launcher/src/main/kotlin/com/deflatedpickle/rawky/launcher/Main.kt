/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.launcher

import ModernDocking.Dockable
import ModernDocking.Docking
import ModernDocking.RootDockingPanel
import com.deflatedpickle.flatlaf.fonts.opendyslexic.FlatOpenDyslexicFont
import com.deflatedpickle.flatlaf.intellijthemes.FlatCatppuccinMacchiatoIJTheme
import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.api.plugin.DependencyComparator
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.event.EventCreatePluginComponent
import com.deflatedpickle.haruhi.event.EventCreatedPluginComponents
import com.deflatedpickle.haruhi.event.EventDeserializedConfig
import com.deflatedpickle.haruhi.event.EventLoadedPlugins
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.event.EventProgramShutdown
import com.deflatedpickle.haruhi.util.ClassGraphUtil
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.haruhi.util.ValidateUtil
import com.deflatedpickle.marvin.util.OSUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.grid.ascii.collection.ASCIICell
import com.deflatedpickle.rawky.grid.pixel.collection.PixelCell
import com.deflatedpickle.rawky.grid.tile.collection.TileCell
import com.deflatedpickle.rawky.launcher.gui.MenuBar
import com.deflatedpickle.rawky.launcher.gui.ToolBar
import com.deflatedpickle.rawky.launcher.gui.Window
import com.deflatedpickle.rawky.launcher.gui.dialog.AboutDialog
import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.util.SystemInfo
import com.jidesoft.plaf.LookAndFeelFactory
import dorkbox.systemTray.SystemTray
import io.github.sanyarnd.applocker.AppLocker
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.apache.logging.log4j.LogManager
import org.fusesource.jansi.AnsiConsole
import org.oxbow.swingbits.dialog.task.TaskDialogs
import org.oxbow.swingbits.util.Strings
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.Dimension
import java.io.File
import javax.swing.Box
import javax.swing.ImageIcon
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JSeparator
import javax.swing.SwingUtilities
import javax.swing.UIManager
import kotlin.system.exitProcess

@InternalSerializationApi
fun main(args: Array<String>) {
    // count the startup time
    val startTime = System.nanoTime()

    // enable terminal colours
    System.setProperty("log4j.skipJansi", "false")
    val logger = LogManager.getLogger()

    logger.debug("Installed JANSI for this terminal session")
    AnsiConsole.systemInstall()

    Haruhi.isInDev = args.contains("indev")

    logger.info(
        """
        |
        |OS  : ${OSUtil.getOS()} (${OSUtil.os})
        |Java: ${System.getProperty("java.version")} (${System.getProperty("java.vm.name")})
        |Dir : ${System.getProperty("user.dir")}
        |Dev?: ${Haruhi.isInDev}
    """
            .trimMargin(),
    )

    val locker = AppLocker.create("com.deflatedpickle.rawky").build()
    locker.lock()

    Haruhi.json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true

        // TODO: have these registered by the plugin's responsible
        serializersModule = SerializersModule {
            polymorphic(Cell::class) {
                subclass(PixelCell::class)
                subclass(TileCell::class)
                subclass(ASCIICell::class)
            }
        }
    }

    if (SystemInfo.isLinux) {
        JFrame.setDefaultLookAndFeelDecorated(true)
        JDialog.setDefaultLookAndFeelDecorated(true)
    }

    if (SystemInfo.isMacOS) {
        System.setProperty("apple.laf.useScreenMenuBar", "true")
        System.setProperty("apple.awt.application.appearance", "system")

        MenuBar.aboutItem.isVisible = false
        MenuBar.exitItem.isVisible = false

        with(Desktop.getDesktop()) {
            if (isSupported(Desktop.Action.APP_ABOUT)) {
                setAboutHandler {
                    val dialog = AboutDialog()
                    dialog.isVisible = true
                }
            }

            if (isSupported(Desktop.Action.APP_QUIT_HANDLER)) {
                setQuitHandler { e, response ->
                    response.performQuit()
                }
            }
        }

        if (SystemInfo.isMacFullWindowContentSupported) {
            Window.rootPane.putClientProperty("apple.awt.fullWindowContent", true)
            Window.rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
            Window.title = null
            ToolBar.add(Box.createHorizontalStrut(70), 0)
        }
    }

    FlatOpenDyslexicFont.install()
    FlatLaf.setPreferredFontFamily(FlatOpenDyslexicFont.FAMILY)
    FlatCatppuccinMacchiatoIJTheme.setup()

    UIManager.put("ModernDocking.titlebar.background.color", UIManager.get("TabbedPane.focusColor"))
    UIManager.put("ModernDocking.titlebar.button.settings", MonoIcon.SETTINGS)
    UIManager.put("ModernDocking.titlebar.button.close", MonoIcon.EXIT)

    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    // SwingUtilities.updateComponentTreeUI(Window)
    LookAndFeelFactory.installJideExtension()

    Haruhi.window = Window
    Haruhi.toastWindow = Window.toastWindow

    SystemTray.get()?.let { icon ->
        icon.setStatus("Rawky")
        icon.setImage(Window.iconImage)

        icon.menu.let { menu ->
            menu.add(JSeparator())
            menu.add(MenuBar.takeScreenshotItem)
            menu.add(MenuBar.aboutItem)
            menu.add(JSeparator())
            menu.add(MenuBar.exitItem)
        }
    }

    Docking.initialize(Window)

    Window.root = RootDockingPanel(Window)
    Window.add(Window.root, BorderLayout.CENTER)

    // Adds a single shutdown thread with an event
    // to reduce the instance count
    Runtime.getRuntime()
        .addShutdownHook(
            object : Thread() {
                override fun run() {
                    logger.debug("Uninstalled JANSI for this terminal session")
                    AnsiConsole.systemUninstall()

                    logger.debug("The JVM instance running Quiver was shutdown")
                    EventProgramShutdown.trigger(true)

                    // Changes were probably made, let's serialize the configs again
                    ConfigUtil.serializeAllConfigs()
                    logger.info("Serialized all the configs")

                    locker.unlock()
                }
            },
        )

    // Handle all uncaught exceptions to open a pop-up
    Thread.setDefaultUncaughtExceptionHandler { t, e ->
        logger.warn("${t.name} threw $e")

        File("error").apply {
            if (!this.exists()) {
                this.mkdirs()
            }

            e::class.simpleName?.let { name ->
                val currentMoment = Clock.System.now()
                val systemZone = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())

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
            TaskDialogs.build().parent(Window).showException(e)
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
        DependencyComparator.thenComparing(Plugin::type).thenComparing(Plugin::value),
    )
    logger.debug(
        "Sorted out the load order: ${PluginUtil.discoveredPlugins.map { PluginUtil.pluginToSlug(it) }}",
    )
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
            plugin.component.objectInstance?.plugin = plugin
            with(plugin.component.objectInstance!!) {
                Docking.registerDockable(this)
                Docking.dock(plugin.component.objectInstance as Dockable, Window)
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
            logger.info(
                "Serialized the config for ${PluginUtil.pluginToSlug(plugin)} to ${file.absolutePath}",
            )
        }
    }

    val loadTime = System.nanoTime()
    logger.debug("Took ${((loadTime - startTime) / 1000) % 60} seconds to load")

    SwingUtilities.invokeLater {
        Window.size = Dimension(1200, 800)
        Window.setLocationRelativeTo(null)

        // This is a catch-all event, used by plugins to run code that depends on setup
        // though the specific events could be used instead
        // For example, if a plugin needs access to a config, they could listen to this
        EventProgramFinishSetup.trigger(true)

        Window.isVisible = true

        val launchTime = System.nanoTime()
        logger.debug("Took ${((launchTime - loadTime) / 1000) % 60} seconds to launch")
    }
}
