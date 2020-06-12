package com.deflatedpickle.rawky.api.util

import bibliothek.gui.dock.common.DefaultSingleCDockable
import com.deflatedpickle.rawky.api.component.RawkyPanel
import com.deflatedpickle.rawky.api.component.RawkyPanelHolder
import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.component.Window
import javax.swing.JScrollBar
import javax.swing.JScrollPane
import kotlin.reflect.full.findAnnotation

object PluginUtil {
    /**
     * A map of plugins that were found when refreshed, paired with the object they were applied to
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val pluginList = mutableMapOf<Any, Plugin>()

    fun discoverPlugins() {
        // Caches the classes found as plugins
        for (plugin in ClassGraphUtil.scanResults.getClassesWithAnnotation(Plugin::class.qualifiedName)) {
            pluginList[plugin.loadClass().kotlin.objectInstance!!] =
                plugin.loadClass().kotlin.objectInstance!!::class.findAnnotation()!!
        }
    }

    fun createComponents() {
        for ((_, annotition) in this.pluginList) {
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