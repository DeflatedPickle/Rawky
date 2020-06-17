package com.deflatedpickle.rawky.api.plugin

enum class PluginType {
    /**
     * Adds a component, such as the pixel grid
     */
    COMPONENT,

    /**
     * Adds a dialog, such as the plugin manager
     */
    DIALOG,

    /**
     * Adds a menu command
     */
    COMMAND_GENERAL,

    /**
     * Does something else
     */
    OTHER,
}