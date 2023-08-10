/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused", "UNCHECKED_CAST")

package com.deflatedpickle.rawky.pixelgrid.mode.keyboard

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.marvin.functions.extensions.get
import com.deflatedpickle.marvin.functions.extensions.set
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.ControlMode
import com.deflatedpickle.rawky.pixelgrid.PixelGridPanel
import com.deflatedpickle.rawky.pixelgrid.mode.keyboard.dialog.KeyboardDialog
import com.deflatedpickle.rawky.pixelgrid.mode.keyboard.util.KeyCombo
import org.jdesktop.swingx.JXButton
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Component
import java.awt.Point
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

@Plugin(
    value = "keyboard",
    author = "DeflatedPickle",
    version = "1.1.0",
    description = """
        <br>
        Use a keyboard to paint
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@pixel_grid#*",
    ],
    settings = KeyboardSettings::class,
)
object KeyboardPlugin : ControlMode() {
    override val name = "Keyboard"
    val point = Point(0, 0)

    private val adapter =
        object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                RawkyPlugin.document?.let { doc ->
                    val frame = doc.children[doc.selectedIndex]
                    val layer = frame.children[frame.selectedIndex]
                    val grid = layer.child

                    ConfigUtil.getSettings<KeyboardSettings>("deflatedpickle@keyboard#*")?.let {
                        when {
                            it.negativeY.accept(e) ->
                                if (point.y - 1 >= 0) {
                                    point.translate(0, -1)
                                } else {
                                    point.move(point.x, grid.rows)
                                }
                            it.positiveY.accept(e) ->
                                if (point.y + 1 <= grid.rows) {
                                    point.translate(0, 1)
                                } else {
                                    point.move(point.x, 0)
                                }
                            it.negativeX.accept(e) ->
                                if (point.x - 1 >= 0) {
                                    point.translate(-1, 0)
                                } else {
                                    point.move(grid.columns, point.y)
                                }
                            it.positiveX.accept(e) ->
                                if (point.x + 1 <= grid.columns) {
                                    point.translate(1, 0)
                                } else {
                                    point.move(0, point.y)
                                }
                            it.useTool.accept(e) ->
                                PixelGridPanel.paint(
                                    MouseEvent.BUTTON1,
                                    e.isShiftDown,
                                    1,
                                )
                        }
                    }

                    if (!e.isShiftDown) {
                        PixelGridPanel.selectedCells.clear()
                    }

                    PixelGridPanel.selectedCells.add(grid[point.x, point.y])
                }
            }
        }

    override fun apply() {
        PixelGridPanel.addKeyListener(adapter)
    }

    override fun remove() {
        PixelGridPanel.removeKeyListener(adapter)
    }

    init {
        registry[name] = this

        EventProgramFinishSetup.addListener {
            (RegistryUtil.get("setting_type") as Registry<String, (Plugin, String, Any) -> Component>?)
                ?.let { registry ->
                    registry.register(KeyCombo::class.qualifiedName!!) { plugin, name, instance ->
                        JXButton().apply {
                            fun genText() =
                                mutableListOf(instance.get<KeyCombo>(name).key)
                                    .apply {
                                        val mod = instance.get<KeyCombo>(name).modifier
                                        if (mod != null) {
                                            add(0, mod)
                                        }
                                    }
                                    .joinToString("+") { k ->
                                        KeyEvent::class
                                            .java
                                            .declaredFields
                                            .firstOrNull { it.name.startsWith("VK_") && it.get(null) == k }
                                            ?.name
                                            ?: ""
                                    }

                            text = genText()

                            addActionListener {
                                val dialog = KeyboardDialog()
                                dialog.isVisible = true

                                if (dialog.result == TaskDialog.StandardCommand.OK) {
                                    val k = instance.get<KeyCombo>(name)

                                    instance.set(
                                        name,
                                        KeyCombo(
                                            dialog.keyboard.selectedCharacter?.keyCode ?: k.key,
                                            dialog.keyboard.selectedModifier?.keyCode,
                                        ),
                                    )
                                    ConfigUtil.serializeConfig(plugin)

                                    text = genText()
                                }
                            }
                        }
                    }
                }
        }
    }
}
