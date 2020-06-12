package com.deflatedpickle.rawky.component

import bibliothek.gui.dock.common.CControl
import bibliothek.gui.dock.common.CGrid
import com.deflatedpickle.rawky.event.EventMenuBarBuild
import com.deflatedpickle.rawky.menu.MenuBar
import com.deflatedpickle.rawky.menu.MenuTools
import org.apache.logging.log4j.LogManager
import javax.swing.JFrame
import javax.swing.JMenuBar

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