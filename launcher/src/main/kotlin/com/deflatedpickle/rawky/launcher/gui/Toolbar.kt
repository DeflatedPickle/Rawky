/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.launcher.gui

import com.deflatedpickle.haruhi.util.PluginUtil
import javax.swing.JToolBar

object Toolbar : JToolBar() {
    init {
        PluginUtil.toolbar = this
    }
}