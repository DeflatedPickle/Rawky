package com.deflatedpickle.rawky.launcher.gui

import bibliothek.gui.dock.common.CControl
import bibliothek.gui.dock.common.CGrid
import com.deflatedpickle.tosuto.ToastWindow
import java.awt.BorderLayout
import javax.swing.JFrame

object Window : JFrame("Rawky") {
    val control = CControl(this)
    val grid = CGrid(control)

    val toastWindow = ToastWindow(
        parent = this,
        toastWidth = 160
    )

    init {
        this.defaultCloseOperation = EXIT_ON_CLOSE

        jMenuBar = MenuBar

        add(Toolbar, BorderLayout.PAGE_START)
        this.add(control.contentArea, BorderLayout.CENTER)

        this.pack()
    }

    override fun setVisible(b: Boolean) {
        super.setVisible(b)
        toastWindow.isVisible = b
    }
}