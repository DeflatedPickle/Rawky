/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.component

import bibliothek.gui.dock.common.DefaultSingleCDockable
import bibliothek.gui.dock.common.event.CFocusListener
import bibliothek.gui.dock.common.intern.CDockable
import com.deflatedpickle.rawky.component.ActionHistory
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.extension.fromCamelCaseToConstantCase
import com.deflatedpickle.rawky.util.extension.toCamelCase

open class ActionComponent : Component() {
    val actionStack = ActionStack()

    init {
        Components.cControl.addFocusListener(object : CFocusListener {
            override fun focusGained(dockable: CDockable) {
                if (dockable is DefaultSingleCDockable) {
                    when (val component = dockable.contentPane.components.last()) {
                        is Component -> {
                        }
                        is ComponentFrame -> {
                            val comp = component.component

                            if (comp is ActionComponent) {
                                ActionHistory.currentWidget = comp
                                ActionHistory.refresh()

                                ActionHistory.comboboxCurrentWidget.selectedIndex = ActionHistory.comboboxValues.indexOf(
                                        ActionHistory.comboboxValues.find {
                                            it == comp::class.simpleName!!
                                                    .fromCamelCaseToConstantCase()
                                                    .toCamelCase()
                                        }
                                )
                            }
                        }
                    }
                }
            }

            override fun focusLost(dockable: CDockable) {
            }
        })
    }
}
