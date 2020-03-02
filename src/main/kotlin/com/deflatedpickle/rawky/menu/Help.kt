/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.menu

import com.deflatedpickle.rawky.dialogue.About
import javax.swing.JMenu
import javax.swing.JMenuItem

class Help : JMenu("Help") {
    init {
        add(JMenuItem("About").apply { addActionListener { About().show() } })
    }
}
