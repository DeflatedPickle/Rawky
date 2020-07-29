package com.deflatedpickle.rawky.event.specific

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.rawky.event.AbstractEvent

/**
 * Called when the load order of plugins is sorted
 */
object EventSortedPluginLoadOrder : AbstractEvent<List<Plugin>>()