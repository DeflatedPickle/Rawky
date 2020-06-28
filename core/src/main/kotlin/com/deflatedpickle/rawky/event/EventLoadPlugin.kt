package com.deflatedpickle.rawky.event

import com.deflatedpickle.rawky.api.plugin.Plugin

/**
 * Called when a plugin is loaded, with the plugin annotation instance
 */
object EventLoadPlugin : AbstractEvent<Plugin>()