package com.deflatedpickle.rawky.event.specific

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.event.AbstractEvent

/**
 * Called when the load order of plugins is sorted
 */
object EventSortedPluginLoadOrder : AbstractEvent<List<Plugin>>()