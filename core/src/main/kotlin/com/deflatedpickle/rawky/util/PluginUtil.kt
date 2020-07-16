package com.deflatedpickle.rawky.util

import bibliothek.gui.dock.common.CLocation
import bibliothek.gui.dock.common.DefaultSingleCDockable
import bibliothek.gui.dock.common.event.CFocusListener
import bibliothek.gui.dock.common.intern.CDockable
import com.deflatedpickle.rawky.api.ComponentPosition
import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.api.plugin.PluginType
import com.deflatedpickle.rawky.event.reusable.EventDiscoverPlugin
import com.deflatedpickle.rawky.event.reusable.EventLoadPlugin
import com.deflatedpickle.rawky.ui.component.RawkyPanel
import com.deflatedpickle.rawky.ui.component.RawkyPanelHolder
import com.deflatedpickle.rawky.ui.window.Window
import com.deflatedpickle.rawky.event.reusable.EventPanelFocusGained
import com.deflatedpickle.rawky.event.reusable.EventPanelFocusLost
import io.github.classgraph.ClassInfo
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.apache.logging.log4j.LogManager
import java.io.File
import javax.swing.JScrollPane
import kotlin.reflect.KClass

object PluginUtil {
    private val logger = LogManager.getLogger(this::class.simpleName)

    /**
     * A list of found plugins, ordered for dependencies
     */
    val discoveredPlugins = mutableListOf<Plugin>()

    /**
     * A list of loaded plugins, unordered
     */
    val loadedPlugins = mutableListOf<Plugin>()

    /**
     * A list of plugins that threw errors during loading
     */
    val unloadedPlugins = mutableListOf<Plugin>()

    val slugToPlugin = mutableMapOf<String, Plugin>()

    // These are used to validate different strings
    private val versionRegex = Regex("[0-9].[0-9].[0-9]")
    private val slugRegex = Regex("[a-z]+@[a-z_]+#[0-9].[0-9].[0-9]")

    /**
     * A map of plugins that were found when refreshed, paired with the object they were applied to
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val pluginMap = mutableMapOf<Plugin, ClassInfo>()

    /**
     * Constructs a slug from a [Plugin]
     */
    fun pluginToSlug(plugin: Plugin): String =
        "${plugin.author.toLowerCase()}@${plugin.value.toLowerCase()}#${plugin.version}"

    /**
     * Creates the plugins folder
     */
    // This can throw an error, but we won't umbrella it
    // in case the user needs to see it
    fun createPluginsFolder(): File =
        File("plugins").apply { mkdir() }

    /**
     * Calls [run] for all classes annotated with [Plugin], to find and validate each plugin
     */
    fun discoverPlugins() {
        // Caches the classes found as plugins
        var counter = 0

        for (plugin in ClassGraphUtil.scanResults.getClassesWithAnnotation(Plugin::class.qualifiedName)) {
            logger.debug("Found the plugin ${plugin.simpleName} from ${plugin.packageName}")

            val annotation = ClassGraphUtil.scanResults
                .getClassInfo(plugin.name)
                .getAnnotationInfo(Plugin::class.qualifiedName)
                .loadClassAndInstantiate() as Plugin

            this.discoveredPlugins.add(annotation)

            this.pluginMap[annotation] = plugin

            this.slugToPlugin[
                    this.pluginToSlug(annotation)
            ] = annotation

            EventDiscoverPlugin.trigger(annotation)
            counter++
        }

        this.logger.info("Found $counter plugin/s")
    }

    fun loadPlugins(validator: (Plugin) -> Boolean) {
        var counter = 0

        for (ann in discoveredPlugins) {
            if (validator(ann)) {
                this.pluginMap[ann]!!.loadClass().kotlin.objectInstance
                this.loadedPlugins.add(ann)
                EventLoadPlugin.trigger(ann)

                counter++
            } else {
                this.unloadedPlugins.add(ann)
            }
        }

        this.logger.info("Loaded $counter plugin/s")
    }

    /**
     * Checks [plugin]'s version matches [versionRegex]
     */
    fun validateVersion(plugin: Plugin): Boolean {
        if (this.versionRegex.containsMatchIn(plugin.version)) {
            return true
        }

        this.logger.warn("The plugin ${plugin.value} doesn't have a valid version. Please use a semantic version")
        return false
    }

    /**
     * Checks [plugin]'s description includes a line break
     */
    fun validateDescription(plugin: Plugin): Boolean {
        if (plugin.description.contains("<br>")) {
            return true
        }

        this.logger.warn("The plugin ${plugin.value} doesn't contain a break tag")
        return false
    }

    /**
     * Checks [plugin]'s type has the proper field
     */
    fun validateType(plugin: Plugin): Boolean =
        when (plugin.type) {
            PluginType.CORE_API,
            PluginType.LAUNCHER,
            PluginType.API,
            PluginType.MENU_COMMAND,
            PluginType.DIALOG,
            PluginType.OTHER -> true

            PluginType.SETTING -> plugin.settings != Nothing::class
            PluginType.COMPONENT -> plugin.component != Nothing::class
        }

    /**
     * Checks [plugin]'s dependency slug's matches [slugRegex]
     */
    fun validateDependencySlug(plugin: Plugin): Boolean {
        for (dep in plugin.dependencies) {
            if (!this.slugRegex.matches(dep)) {
                return false
            }
        }

        return true
    }

    /**
     * Checks [plugin]'s dependencies exist
     */
    fun validateDependencyExistence(plugin: Plugin): Boolean {
        val suggestions = mutableMapOf<String, MutableList<String>>()

        for (dep in plugin.dependencies) {
            if (dep !in this.discoveredPlugins.map { this.pluginToSlug(it) }) {
                suggestions.putIfAbsent(dep, mutableListOf())

                for (checkDep in this.pluginMap.keys) {
                    if (FuzzySearch.partialRatio(dep, checkDep.value) > 60) {
                        suggestions[dep]!!.add(checkDep.value)
                    }
                }
            }
        }

        if (suggestions.isEmpty()) {
            return true
        }

        for ((k, v) in suggestions) {
            this.logger.warn("The plugin ID \"$k\" wasn't found. Did you mean one of these? $v")
        }

        return false
    }

    /**
     * Creates all plugin components
     */
    fun createComponent(plugin: Plugin, panel: RawkyPanel): RawkyPanel {
        panel.plugin = plugin
        panel.scrollPane = JScrollPane(panel)

        panel.componentHolder = RawkyPanelHolder()
        panel.componentHolder.dock = DefaultSingleCDockable(
            plugin.value,
            plugin.value.replace("_", " ").capitalize(),
            panel.scrollPane
        )
        panel.componentHolder.dock.addFocusListener(object : CFocusListener {
            override fun focusLost(dockable: CDockable) {
                EventPanelFocusLost.trigger(
                    ((dockable as DefaultSingleCDockable)
                        .contentPane
                        .getComponent(0) as JScrollPane)
                        .viewport
                        .view as RawkyPanel
                )
            }

            override fun focusGained(dockable: CDockable) {
                EventPanelFocusGained.trigger(
                    ((dockable as DefaultSingleCDockable)
                        .contentPane
                        .getComponent(0) as JScrollPane)
                        .viewport
                        .view as RawkyPanel
                )
            }
        })

        Window.control.addDockable(panel.componentHolder.dock as DefaultSingleCDockable)

        panel.componentHolder.dock.setLocation(
            when (plugin.componentPosition) {
                ComponentPosition.NORTH -> CLocation.base().minimalNorth()
                ComponentPosition.EAST -> CLocation.base().minimalEast()
                ComponentPosition.SOUTH -> CLocation.base().minimalSouth()
                ComponentPosition.WEST -> CLocation.base().minimalWest()
            }
        )

        panel.componentHolder.dock.isVisible = true

        return panel
    }
}