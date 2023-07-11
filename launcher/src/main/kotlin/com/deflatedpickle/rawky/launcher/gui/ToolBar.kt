/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.launcher.gui

import ModernDocking.DockingState
import ModernDocking.layouts.DockingLayouts
import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.launcher.LauncherPlugin
import com.deflatedpickle.rawky.util.ActionUtil
import com.deflatedpickle.undulation.functions.extensions.add
import java.awt.Dimension
import java.awt.event.ItemEvent
import javax.swing.Box
import javax.swing.JComboBox
import javax.swing.JToolBar

object ToolBar : JToolBar() {
    val layoutComboBox =
        JComboBox(arrayOf<String>()).apply {
            maximumSize = Dimension(200, preferredSize.height)

            addItemListener {
                when (it.stateChange) {
                    ItemEvent.SELECTED -> {
                        DockingState.restoreApplicationLayout(
                            DockingLayouts.getLayout((it.item as String).lowercase()),
                        )
                    }
                }
            }

            EventProgramFinishSetup.addListener {
                for (l in DockingLayouts.getLayoutNames()) {
                    addItem(l.capitalize())
                }
            }
        }

    init {
        Haruhi.toolbar = this

        add(icon = MonoIcon.FOLDER_NEW, tooltip = "New") { ActionUtil.newFile() }
        add(icon = MonoIcon.FOLDER_OPEN, tooltip = "Open") {
            LauncherPlugin.openDialog(MenuBar.fileMenu)
        }

        addSeparator()

        add(icon = MonoIcon.FILE_NEW, tooltip = "Import") { LauncherPlugin.importDialog() }
        add(icon = MonoIcon.FILE_EXPORT, tooltip = "Export") { LauncherPlugin.exportDialog() }

        addSeparator()

        add(Box.createHorizontalGlue())

        add(layoutComboBox)
    }
}
