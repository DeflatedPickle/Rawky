package com.deflatedpickle.rawky.util

import bibliothek.gui.dock.common.DefaultSingleCDockable
import bibliothek.gui.dock.common.event.CFocusListener
import bibliothek.gui.dock.common.intern.CDockable
import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.api.plugin.PluginType
import com.deflatedpickle.rawky.ui.component.RawkyPanel
import com.deflatedpickle.rawky.ui.component.RawkyPanelHolder
import com.deflatedpickle.rawky.ui.window.Window
import com.deflatedpickle.rawky.event.EventLoadPlugin
import com.deflatedpickle.rawky.event.EventLoadedPlugins
import com.deflatedpickle.rawky.event.EventPanelFocusGained
import com.deflatedpickle.rawky.event.EventPanelFocusLost
import io.github.classgraph.ClassInfo
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.apache.logging.log4j.LogManager
import java.io.File
import javax.swing.JScrollPane
import kotlin.reflect.full.findAnnotation

object PluginUtil {
    private val logger = LogManager.getLogger(this::class.simpleName)

    /**
     * A list of loaded plugins, ordered for dependencies
     */
    val pluginLoadOrder = mutableListOf<Plugin>()

    /**
     * A list of plugins that threw errors during loading
     */
    val unloadedPlugins = mutableListOf<Plugin>()

    val slugToPlugin = mutableMapOf<String, Plugin>()

    // These are used to validate different strings
    private val versionRegex = Regex("[0-9].[0-9].[0-9]")
    private val slugRegex = Regex("[a-z0-9;._]+")

    /**
     * A map of plugins that were found when refreshed, paired with the object they were applied to
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val pluginMap = mutableMapOf<Plugin, ClassInfo>()

    fun pluginToSlug(plugin: Plugin): String =
        "${plugin.author.toLowerCase()};${plugin.value.toLowerCase()};${plugin.version}"

    fun createPluginsFolder() {
        // This can throw an error, but we won't umbrella it
        // in case the user needs to see it
        File("plugins").mkdir()
    }

    fun discoverPlugins(run: (Plugin) -> Boolean) {
        // Caches the classes found as plugins
        var counter = 0

        for (plugin in ClassGraphUtil.scanResults.getClassesWithAnnotation(Plugin::class.qualifiedName)) {
            logger.debug("Found the plugin ${plugin.simpleName} from ${plugin.packageName}")

            val annotation = ClassGraphUtil.scanResults
                .getClassInfo(plugin.name)
                .getAnnotationInfo(Plugin::class.qualifiedName)
                .loadClassAndInstantiate() as Plugin

            if (run(annotation)) {
                this.pluginLoadOrder.add(annotation)

                this.pluginMap[annotation] = plugin
                this.slugToPlugin[
                    this.pluginToSlug(annotation)
                ] = annotation

                counter++
            } else {
                this.unloadedPlugins.add(annotation)
            }
        }

        this.logger.info("Found $counter plugin/s")
    }

    fun validateVersion(plugin: Plugin): Boolean {
        if (this.versionRegex.containsMatchIn(plugin.version)) {
            return true
        }

        this.logger.warn("The plugin ${plugin.value} doesn't have a valid version. Please use a semantic version")
        return false
    }

    fun validateDescription(plugin: Plugin): Boolean {
        if (plugin.description.contains("<br>")) {
            return true
        }

        this.logger.warn("The plugin ${plugin.value} doesn't contain a break tag")
        return false
    }

    fun validateType(plugin: Plugin): Boolean =
        when (plugin.type) {
            PluginType.CORE_API,
            PluginType.API,
            PluginType.MENU_COMMAND,
            PluginType.DIALOG,
            PluginType.OTHER -> true

            PluginType.SETTING -> plugin.settings != Nothing::class
            PluginType.COMPONENT -> plugin.components != Nothing::class
        }

    fun validateDependencySlug(plugin: Plugin): Boolean {
        for (dep in plugin.dependencies) {
            if (!this.slugRegex.matches(dep)) {
                return false
            }
        }

        return true
    }

    fun validateDependencyExistence(plugin: Plugin): Boolean {
        val suggestions = mutableMapOf<String, MutableList<String>>()

        for (dep in plugin.dependencies) {
            if (dep !in this.pluginLoadOrder.map { this.pluginToSlug(it) }) {
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

    fun figureOutLoadOrder() {
        this.pluginLoadOrder.sortWith(Plugin.comparator)

        this.logger.info("Sorted out the load order: ${this.pluginLoadOrder.map { this.pluginToSlug(it) }}")
    }

    fun loadPlugins() {
        for (i in this.pluginLoadOrder) {
            this.pluginMap[i]!!.loadClass().kotlin.objectInstance
            EventLoadPlugin.trigger(i)
        }
        EventLoadedPlugins.trigger(this.pluginLoadOrder)
    }

    fun createComponents() {
        for (i in this.pluginLoadOrder) {
            val plugin = this.pluginMap[i]!!.loadClass().kotlin.objectInstance!!
            // haha tit
            val annotition = plugin::class.findAnnotation<Plugin>()!!

            for (comp in annotition.components) {
                val panel = comp.objectInstance!!
                panel.plugin = annotition
                panel.scrollPane = JScrollPane(panel)

                panel.componentHolder = RawkyPanelHolder()
                panel.componentHolder.dock = DefaultSingleCDockable(
                    annotition.value,
                    annotition.value.replace("_", " ").capitalize(),
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

                Window.grid.add(
                    0.0, 0.0,
                    0.0, 0.0,
                    panel.componentHolder.dock
                )
            }
        }
    }
}