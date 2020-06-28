package com.deflatedpickle.rawky.util

import bibliothek.gui.dock.common.DefaultSingleCDockable
import bibliothek.gui.dock.common.event.CFocusListener
import bibliothek.gui.dock.common.intern.CDockable
import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.ui.component.RawkyPanel
import com.deflatedpickle.rawky.ui.component.RawkyPanelHolder
import com.deflatedpickle.rawky.ui.window.Window
import com.deflatedpickle.rawky.event.EventLoadPlugin
import com.deflatedpickle.rawky.event.EventLoadedPlugins
import com.deflatedpickle.rawky.event.EventPanelFocusGained
import com.deflatedpickle.rawky.event.EventPanelFocusLost
import com.deflatedpickle.rawky.function.umbrella
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

    val idToPlugin = mutableMapOf<String, Plugin>()

    /**
     * A map of plugins that were found when refreshed, paired with the object they were applied to
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val pluginMap = mutableMapOf<Plugin, ClassInfo>()

    fun createPluginsFolder() {
        // This can throw an error, but we won't umbrella it
        // in case the user needs to see it
        File("plugins").mkdir()
    }

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

            this.idToPlugin[annotation.value] = annotation

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
            umbrella(this.logger) {
                this.pluginMap[i]!!.loadClass().kotlin.objectInstance
            }
            EventLoadPlugin.trigger(i)
        }
        EventLoadedPlugins.trigger(this.pluginLoadOrder)
    }

    fun createComponents() {
        for (i in this.pluginLoadOrder) {
            umbrella(this.logger) {
                val plugin = this.pluginMap[i]!!.loadClass().kotlin.objectInstance!!
                // haha tit
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
}