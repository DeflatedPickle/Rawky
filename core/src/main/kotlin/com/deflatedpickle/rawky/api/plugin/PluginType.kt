package com.deflatedpickle.rawky.api.plugin

enum class PluginType {
    /**
     * The core API of the program
     */
    CORE_API,

    /**
     * A launcher for the program
     */
    LAUNCHER,

    /**
     * Adds an API for other plugins to use
     */
    API,

    /**
     * Adds a setting
     */
    SETTING,

    /**
     * Adds a menu command
     */
    MENU_COMMAND,

    /**
     * Adds a component, such as the pixel grid
     */
    COMPONENT,

    /**
     * Adds a dialog, such as the plugin manager
     */
    DIALOG,

    /**
     * Does something else
     */
    OTHER,
}