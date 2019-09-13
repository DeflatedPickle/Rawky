package com.deflatedpickle.rawky.component

import java.awt.BorderLayout
import javax.swing.DefaultListModel
import javax.swing.JList
import javax.swing.JPanel

class ActionHistory : JPanel() {
    val listModel = DefaultListModel<String>()
    val list = JList<String>(listModel)

    init {
        isOpaque = false
        layout = BorderLayout()

        add(list)
    }
}