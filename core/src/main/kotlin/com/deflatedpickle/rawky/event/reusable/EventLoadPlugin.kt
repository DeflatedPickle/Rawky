package com.deflatedpickle.rawky.event.reusable

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.event.AbstractEvent

/**
 * Called when a plugin is loaded, with the plugin annotation instance
 */
object EventLoadPlugin : AbstractEvent<Plugin>()