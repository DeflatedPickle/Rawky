package com.deflatedpickle.rawky.ui.menu

import com.deflatedpickle.rawky.event.EventMenuBarBuild
import com.deflatedpickle.rawky.event.EventMenuBuild
import javax.swing.JMenuBar

object MenuBar : JMenuBar() {
    init {
        EventMenuBarBuild.trigger(this)

        EventMenuBuild.trigger(MenuTools)
        this.add(MenuTools)
    }
}