package com.deflatedpickle.rawky.ui.menu

import com.deflatedpickle.rawky.event.EventMenuBarAdd
import com.deflatedpickle.rawky.event.EventMenuBarBuild
import com.deflatedpickle.rawky.event.EventMenuBuild
import javax.swing.JMenu
import javax.swing.JMenuBar

object MenuBar : JMenuBar() {
    init {
        EventMenuBarBuild.trigger(this)

        for (i in arrayOf(
            MenuFile,
            MenuTools
        )) {
            this.addMenu(i)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun addMenu(menu: JMenu) {
        EventMenuBuild.trigger(menu)
        this.add(menu)
        EventMenuBarAdd.trigger(menu)
    }
}