/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.launcher.gui

import com.deflatedpickle.haruhi.Haruhi
import javax.swing.JToolBar

object Toolbar : JToolBar() {
    init {
        Haruhi.toolbar = this
    }
}
