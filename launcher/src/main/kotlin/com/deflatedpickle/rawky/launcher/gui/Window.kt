/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.launcher.gui

import ModernDocking.RootDockingPanel
import com.deflatedpickle.tosuto.ToastWindow
import java.awt.BorderLayout
import javax.swing.JFrame

object Window : JFrame("Rawky") {
    lateinit var root: RootDockingPanel

    val toastWindow = ToastWindow(parent = this, toastWidth = 160)

    init {
        this.defaultCloseOperation = EXIT_ON_CLOSE

        jMenuBar = MenuBar

        add(ToolBar, BorderLayout.NORTH)
        add(StatusBar, BorderLayout.SOUTH)

        pack()
    }

    override fun setVisible(b: Boolean) {
        super.setVisible(b)
        toastWindow.isVisible = b
    }
}
