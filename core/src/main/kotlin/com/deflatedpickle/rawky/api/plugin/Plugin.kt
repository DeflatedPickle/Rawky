package com.deflatedpickle.rawky.api.plugin

import org.apache.commons.lang3.StringUtils
import kotlin.reflect.KClass

/**
 * A plugin to add extra features
 *
 * Discovered by [PluginManager.discoverPlugins]
 */
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
     * The description of the plugin
     *
     * This will be chopped at 60 characters for the short description.
     */
    val description: String = StringUtils.EMPTY,
    /**
     * The components this plugin provides
     */
    val components: Array<KClass<*>> = []
)