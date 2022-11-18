/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.marvin.extensions.get
import com.deflatedpickle.marvin.extensions.set
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.rawky.pixelgrid.api.Mode
import com.deflatedpickle.rawky.pixelgrid.setting.PixelGridSettings
import kotlinx.serialization.ExperimentalSerializationApi
import java.awt.Component
import java.awt.event.ItemEvent
import javax.swing.JComboBox
import javax.swing.SwingUtilities

@ExperimentalSerializationApi
@Plugin(
    value = "pixel_grid",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        Provides a grid to draw upon
        <br>
        This plugin provides most of the basic user functionality of Rawky.
    """,
    type = PluginType.COMPONENT,
    component = PixelGridPanel::class,
    dependencies = [
        "deflatedpickle@core#*",
    ],
    settings = PixelGridSettings::class
)
@Suppress("unused")
object PixelGridPlugin {
    init {
        EventCreateDocument.addListener {
            PixelGridPanel.repaint()
        }

        EventOpenDocument.addListener {
            PixelGridPanel.repaint()
        }

        EventUpdateGrid.addListener {
            PixelGridPanel.repaint()
        }

        EventProgramFinishSetup.addListener {
            (RegistryUtil.get("setting_type") as Registry<String, (Plugin, String, Any) -> Component>?)?.let { registry ->
                registry.register(Mode::class.qualifiedName!!) { plugin, name, instance ->
                    JComboBox<Mode>().apply {
                        SwingUtilities.invokeLater {
                            for ((_, v) in Mode.registry) {
                                addItem(v)
                            }

                            selectedItem = instance.get<Mode>(name)

                            addItemListener {
                                when (it.stateChange) {
                                    ItemEvent.DESELECTED -> {
                                        (it.item as Mode).remove()
                                    }
                                    ItemEvent.SELECTED -> {
                                        instance.set(name, it.item)
                                        (it.item as Mode).apply()
                                        ConfigUtil.serializeConfig(plugin)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
