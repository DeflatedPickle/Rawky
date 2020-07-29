package com.deflatedpickle.rawky.event.specific

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.rawky.event.AbstractEvent

/**
 * Called once all plugins have been, with an ordered list of the plugins
 */
object EventLoadedPlugins : AbstractEvent<Collection<Plugin>>()