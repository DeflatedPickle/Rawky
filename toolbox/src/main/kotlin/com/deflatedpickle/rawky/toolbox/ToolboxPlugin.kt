@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.rawky.toolbox

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.event.EventChangeTool
import com.deflatedpickle.rawky.toolbox.event.EventToolboxFinish
import com.deflatedpickle.undulation.api.ButtonType
import com.deflatedpickle.undulation.functions.extensions.add
import kotlinx.serialization.ExperimentalSerializationApi
import java.awt.BorderLayout
import javax.swing.ButtonGroup
import javax.swing.JToolBar
import javax.swing.SwingConstants

@Plugin(
    value = "toolbox",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a panel to select tools from
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#*",
    ]
)
@Suppress("unused")
object ToolboxPlugin {
    val toolbar = JToolBar(SwingConstants.VERTICAL)
    val group = ButtonGroup()

    init {
        EventProgramFinishSetup.addListener {
            for (v in Tool.registry.values.sortedBy { it.name }) {
                toolbar.add(
                    icon = v.icon,
                    tooltip = v.name,
                    enabled = false,
                    type = ButtonType.TOGGLE,
                ) {
                    Tool.current = v
                    EventChangeTool.trigger(v)
                }.also { group.add(it) }
            }

            PluginUtil.window.add(toolbar, BorderLayout.LINE_START)

            EventToolboxFinish.trigger(toolbar)
        }

        EventCreateDocument.addListener {
            for (i in group.elements) {
                i.isEnabled = true
            }
        }

        EventOpenDocument.addListener {
            for (i in group.elements) {
                i.isEnabled = true
            }
        }

        EventChangeTool.addListener {
            if (group.elements.toList().any { it.isSelected }) return@addListener

            for (i in group.elements) {
                if (i.icon == Tool.current.icon) {
                    i.isSelected = true
                    break
                }
            }
        }
    }
}