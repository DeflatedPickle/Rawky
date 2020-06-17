package com.deflatedpickle.rawky.ui.component

import bibliothek.gui.dock.common.CControl
import bibliothek.gui.dock.common.CGrid
import com.deflatedpickle.rawky.ui.menu.MenuBar
import javax.swing.JFrame

object Window : JFrame() {
    val control = CControl(this)
    @Suppress("MemberVisibilityCanBePrivate")
    val grid = CGrid(this.control)

    init {
        this.jMenuBar = MenuBar

        this.defaultCloseOperation = EXIT_ON_CLOSE

        this.add(this.control.contentArea)

        this.pack()
    }

    fun deploy() {
        this.control.contentArea.deploy(this.grid)
    }
}