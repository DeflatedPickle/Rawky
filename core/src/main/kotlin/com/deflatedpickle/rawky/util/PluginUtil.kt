package com.deflatedpickle.rawky.util

import bibliothek.gui.dock.common.DefaultSingleCDockable
import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.component.RawkyPanel
import com.deflatedpickle.rawky.component.RawkyPanelHolder
import com.deflatedpickle.rawky.component.Window
import com.deflatedpickle.rawky.event.EventLoadPlugin
import io.github.classgraph.ClassInfo
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.apache.logging.log4j.LogManager
import javax.swing.JScrollPane
import kotlin.reflect.full.findAnnotation

object PluginUtil {
    private val logger = LogManager.getLogger(this::class.simpleName)

    /**
     * A list of loaded plugins, ordered for dependencies
     */
    val pluginLoadOrder = mutableListOf<Plugin>()

    /**
     * A map of plugins that were found when refreshed, paired with the object they were applied to
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val pluginMap = mutableMapOf<Plugin, ClassInfo>()

    fun discoverPlugins() {
        // Caches the classes found as plugins
        var counter = 0
        for (plugin in ClassGraphUtil.scanResults.getClassesWithAnnotation(Plugin::class.qualifiedName)) {
            logger.debug("Found the plugin ${plugin.simpleName} from ${plugin.packageName}")

            val annotation = ClassGraphUtil.scanResults
                .getClassInfo(plugin.name)
                .getAnnotationInfo(Plugin::class.qualifiedName)
                .loadClassAndInstantiate() as Plugin

            this.pluginMap[annotation] = plugin
            this.pluginLoadOrder.add(annotation)

            counter++
        }

        this.logger.info("Found $counter plugin/s")
    }

    fun validateDependencies() {
        for (plug in this.pluginLoadOrder) {
            val suggestions = mutableMapOf<String, MutableList<String>>()

            for (dep in plug.dependencies) {
                if (dep == "all") continue

                if (dep !in this.pluginLoadOrder.map { it.value }) {
                    suggestions.putIfAbsent(dep, mutableListOf())

                    for (checkDep in this.pluginMap.keys) {
                        if (FuzzySearch.partialRatio(dep, checkDep.value) > 60) {
                            suggestions[dep]!!.add(checkDep.value)
                        }
                    }
                }
            }

            for ((k, v) in suggestions) {
                this.logger.warn("The plugin ID \"$k\" wasn't found. Did you mean one of these? $v")
            }
        }
    }

    fun figureOutLoadOrder() {
        this.pluginLoadOrder.sortWith(Plugin.comparator)

        this.logger.info("Sorted out the load order: ${this.pluginLoadOrder.map { it.value }}")
    }

    fun loadPlugins() {
        for (i in this.pluginLoadOrder) {
            this.pluginMap[i]!!.loadClass().kotlin.objectInstance
            EventLoadPlugin.trigger(i)
        }
    }

    fun createComponents() {
        for (i in this.pluginLoadOrder) {
            val plugin = this.pluginMap[i]!!.loadClass().kotlin.objectInstance!!
            val annotition = plugin::class.findAnnotation<Plugin>()!!

            for (comp in annotition.components) {
                val panel = comp.objectInstance!! as RawkyPanel
                panel.plugin = annotition
                panel.scrollPane = JScrollPane(panel)

                panel.componentHolder = RawkyPanelHolder()
                panel.componentHolder.dock = DefaultSingleCDockable(
                    annotition.value,
                    annotition.value.replace("_", " ").capitalize(),
                    panel.scrollPane
                )

                Window.grid.add(
                    0.0, 0.0,
                    0.0, 0.0,
                    panel.componentHolder.dock
                )
            }
        }
    }
}