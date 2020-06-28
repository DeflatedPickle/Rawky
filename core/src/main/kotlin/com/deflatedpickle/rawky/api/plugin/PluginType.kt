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
    MENU_COMMAND,

    /**
     * Adds a setting
     */
    SETTING,

    /**
     * Adds an API for other plugins to use
     */
    API,

    /**
     * Does something else
     */
    OTHER,
}