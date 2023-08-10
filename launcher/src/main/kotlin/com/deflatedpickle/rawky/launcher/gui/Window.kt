/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.launcher.gui

import ModernDocking.RootDockingPanel
import com.deflatedpickle.marvin.functions.extensions.getLastModifiedTime
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.launcher.LauncherPlugin
import com.deflatedpickle.tosuto.ToastWindow
import java.awt.BorderLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.time.ZoneId
import javax.swing.JFrame
import javax.swing.JOptionPane
import kotlin.system.exitProcess

object Window : JFrame("Rawky") {
    lateinit var root: RootDockingPanel

    val toastWindow = ToastWindow(
        parent = this,
        toastWidth = 160,
    )

    init {
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        jMenuBar = MenuBar

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                val doc = RawkyPlugin.document
                if (doc != null) {
                    val path = doc.path
                    // the document exists as a file
                    if (path != null && path.exists()) {
                        val modifiedTime = path
                            .getLastModifiedTime()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())

                        // check for unsaved changes
                        if (doc.dirty) {
                            if (JOptionPane.showConfirmDialog(
                                    Window,
                                    "'${path.name}' has unsaved changes. Last saved at '${LauncherPlugin.dateTimeFormatter.format(modifiedTime)}'",
                                    "Close Without Saving?",
                                    JOptionPane.OK_CANCEL_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                ) == JOptionPane.YES_OPTION
                            ) {
                                exitProcess(0)
                            }
                        } else {
                            exitProcess(0)
                        }
                    }
                    // the document is yet to be saved to disk
                    else {
                        if (JOptionPane.showConfirmDialog(
                                Window,
                                "The current document has unsaved changes",
                                "Close Without Saving?",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.WARNING_MESSAGE,
                            ) == JOptionPane.YES_OPTION
                        ) {
                            exitProcess(0)
                        }
                    }
                }
                // there is no document :)
                else {
                    exitProcess(0)
                }
            }
        })

        add(ToolBar, BorderLayout.NORTH)
        add(StatusBar, BorderLayout.SOUTH)

        pack()
    }

    override fun setVisible(b: Boolean) {
        super.setVisible(b)
        toastWindow.isVisible = b
    }
}
