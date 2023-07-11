/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.rawky.launcher.gui

import javax.swing.AbstractButton
import javax.swing.JLabel
import javax.swing.JPopupMenu
import javax.swing.JToolBar
import javax.swing.MenuElement
import javax.swing.SwingUtilities

object StatusBar : JToolBar() {
    var note = " "
        set(value) {
            field = value
            noteLabel.text = field

            revalidate()
            repaint()
        }

    private val noteLabel = JLabel(note)

    init {
        add(noteLabel)

        SwingUtilities.invokeLater {
            for (m in 0 until Window.jMenuBar.menuCount) {
                val menu = Window.jMenuBar.getMenu(m)
                recurseMenu(menu)
            }
        }
    }

    private fun recurseMenu(item: MenuElement) {
        if (item is AbstractButton) {
            item.addChangeListener {
                note = (it.source as AbstractButton).getClientProperty("statusMessage") as String? ?: " "

                for (i in item.subElements) {
                    recurseMenu(i)
                }
            }
        } else if (item is JPopupMenu) {
            for (i in item.subElements) {
                recurseMenu(i)
            }
        }
    }
}
