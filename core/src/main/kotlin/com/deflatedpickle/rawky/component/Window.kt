package com.deflatedpickle.rawky.component

import bibliothek.gui.dock.common.CControl
import bibliothek.gui.dock.common.CGrid
import javax.swing.JFrame

object Window : JFrame() {
    val control = CControl(this)
    @Suppress("MemberVisibilityCanBePrivate")
    val grid = CGrid(this.control)

    init {
        this.defaultCloseOperation = EXIT_ON_CLOSE

        this.add(this.control.contentArea)

        this.pack()
    }

    fun deploy() {
        this.control.contentArea.deploy(this.grid)
    }
}