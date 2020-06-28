package com.deflatedpickle.rawky.event

import com.deflatedpickle.rawky.api.plugin.Plugin

/**
 * Called once all plugins have been, with an ordered list of the plugins
 */
object EventLoadedPlugins : AbstractEvent<Collection<Plugin>>()