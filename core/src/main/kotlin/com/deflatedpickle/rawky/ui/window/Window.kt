package com.deflatedpickle.rawky.ui.window

import bibliothek.gui.dock.common.CControl
import bibliothek.gui.dock.common.CGrid
import com.deflatedpickle.rawky.event.specific.EventToastWindowShown
import com.deflatedpickle.rawky.ui.menu.MenuBar
import com.deflatedpickle.tosuto.ToastWindow
import javax.swing.JFrame

object Window : JFrame() {
    @Suppress("MemberVisibilityCanBePrivate")
    val control = CControl(this)

    @Suppress("MemberVisibilityCanBePrivate")
    val grid = CGrid(control)

    val toastWindow = ToastWindow(this, 160)

    init {
        this.jMenuBar = MenuBar

        this.defaultCloseOperation = EXIT_ON_CLOSE

        this.add(this.control.contentArea)

        this.pack()
    }

    fun deploy() {
        this.control.contentArea.deploy(this.grid)
    }

    override fun setVisible(b: Boolean) {
        super.setVisible(b)
        this.toastWindow.isVisible = true
        EventToastWindowShown.trigger(this.toastWindow)
    }
}