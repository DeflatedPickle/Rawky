package com.deflatedpickle.rawky.api.plugin

import org.apache.commons.lang3.StringUtils
import kotlin.reflect.KClass

import com.deflatedpickle.rawky.util.PluginUtil

/**
 * A plugin to add extra features
 *
 * Discovered by [PluginUtil.discoverPlugins]
 */
@Target(AnnotationTarget.CLASS)
annotation class Plugin(
    /**
     * The name/id of the plugin
     *
     * This should be provided as lower-case, with words separated by underscores.
     *
     * Example: pixel_grid
     */
    val value: String,
    /**
     * The author of the plugin
     *
     * This should be provided as-is.
     *
     * Example: DeflatedPickle
     */
    val author: String,
    /**
     * The version the plugin is currently at
     */
    val version: String,
    /**
     * The description of the plugin
     *
     * This will be chopped at 60 characters for the short description.
     */
    val description: String = "<br>",
    val type: PluginType = PluginType.API,
    /**
     * The components this plugin provides
     */
    val components: Array<KClass<*>> = [],
    /**
     * The plugin IDs this plugin should load after
     */
    val dependencies: Array<String> = [],
    /**
     * The config for this plugin
     */
    val settings: KClass<*> = Nothing::class
) {
    companion object {
        val comparator: Comparator<Plugin> = Comparator<Plugin> { a, b ->
            if (a.dependencies.contains(b.value) || a.type != PluginType.CORE_API) {
                if (b.dependencies.contains(a.value)) {
                    throw IllegalStateException("Circular dependency")
                }
                return@Comparator 1
            }
            return@Comparator 0
        }.thenComparing(Plugin::type).thenComparing(Plugin::value)
    }
}