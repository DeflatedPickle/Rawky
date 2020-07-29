package com.deflatedpickle.rawky.event.specific

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.rawky.event.AbstractEvent

/**
 * Called when all plugin components have been created
 */
object EventCreatedPluginComponents : AbstractEvent<List<PluginPanel>>()