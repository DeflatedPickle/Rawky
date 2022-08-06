@file:Suppress("unused")

package com.deflatedpickle.rawky.tool.shape

import com.alexandriasoftware.swing.JSplitButton
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.event.EventChangeTool
import com.deflatedpickle.rawky.tool.shape.api.Shape
import com.deflatedpickle.rawky.tool.shape.api.Shape.Companion
import com.deflatedpickle.rawky.toolbox.ToolboxPlugin
import com.deflatedpickle.rawky.toolbox.event.EventToolboxFinish
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

@Plugin(
    value = "shape",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Adds a drop down for shapes
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#1.0.0"
    ],
)
object ShapePlugin {
    private val menu = JPopupMenu("Shape")

    init {
        EventProgramFinishSetup.addListener {
            for ((_, v) in Shape.registry) {
                menu.add(
                    JMenuItem(
                        v.name,
                        v.icon,
                    ).apply {
                        ToolboxPlugin.group.add(this)

                        addActionListener {
                            Tool.current = v
                            EventChangeTool.trigger(v)
                        }
                    }
                )
            }
        }

        EventToolboxFinish.addListener {
            ToolboxPlugin.toolbar.add(
                JSplitButton(MonoIcon.SHAPES).apply {
                    popupMenu = menu
                    isAlwaysPopup = true
                }
            )
        }
    }
}