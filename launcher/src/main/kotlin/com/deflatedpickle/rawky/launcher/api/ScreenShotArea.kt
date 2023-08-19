package com.deflatedpickle.rawky.launcher.api

enum class ScreenShotArea {
    PROGRAM,
    // TODO: window
    SCREEN,
    // TODO: region
    ;

    override fun toString() = this.name.lowercase().capitalize()
}