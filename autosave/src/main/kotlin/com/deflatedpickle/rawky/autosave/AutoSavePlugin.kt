@file:Suppress("SpellCheckingInspection", "UNCHECKED_CAST")

package com.deflatedpickle.rawky.autosave

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.marvin.extensions.get
import com.deflatedpickle.marvin.extensions.set
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.impex.Exporter
import com.deflatedpickle.rawky.autosave.event.EventAutoSaveDocument
import com.deflatedpickle.rawky.autosave.util.FileType
import com.deflatedpickle.rawky.settings.SettingsGUI
import com.deflatedpickle.undulation.constraints.FillHorizontal
import java.awt.Component
import java.awt.GridBagLayout
import java.awt.Panel
import java.io.File
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.Timer

@Plugin(
    value = "auto_save",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Adds a timer to automatically save the current file
    """,
    dependencies = [
        "deflatedpickle@core#*",
    ],
    settings = AutoSaveSettings::class
)
@Suppress("unused")
object AutoSavePlugin {
    // Miliseconds in a minute
    private const val MINUTE = 60000

    private var timer: Timer? = null

    init {
        EventProgramFinishSetup.addListener {
            ConfigUtil.getSettings<AutoSaveSettings>("deflatedpickle@auto_save#*")?.let { config ->
                if (config.fileType == null) {
                    val exporter = Exporter.registry.values.first()
                    config.fileType = FileType(exporter, exporter.extensions.first())

                    PluginUtil.slugToPlugin("deflatedpickle@auto_save#*")
                        ?.let { plug -> ConfigUtil.serializeConfig(plug) }
                }
            }

            (RegistryUtil.get("setting_type") as Registry<String, (Plugin, String, Any) -> Component>?)?.let { registry ->
                registry.register(FileType::class.qualifiedName!!) { plugin, name, instance ->
                    Panel(GridBagLayout()).apply {
                        val inst = instance.get<FileType>(name)

                        val handler = JComboBox(Exporter.registry.values.toTypedArray()).apply {
                            selectedItem = inst.handler

                            setRenderer { _, value, _, _, _ ->
                                JLabel((value as Exporter).name)
                            }
                        }
                        val extension = JComboBox((handler.selectedItem as Exporter).extensions.toTypedArray()).apply {
                            selectedItem = inst.extension
                        }

                        handler.addItemListener {
                            (extension.model as DefaultComboBoxModel).apply {
                                removeAllElements()
                                addAll((handler.selectedItem as Exporter).extensions)
                            }
                            extension.selectedIndex = 0

                            instance.set(name, FileType(
                                handler.selectedItem as Exporter,
                                extension.selectedItem as String? ?: "null",
                            ))
                            ConfigUtil.serializeConfig(plugin)
                        }

                        extension.addItemListener {
                            instance.set(name, FileType(
                                handler.selectedItem as Exporter,
                                extension.selectedItem as String? ?: "null",
                            ))
                            ConfigUtil.serializeConfig(plugin)
                        }

                        add(handler, FillHorizontal)
                        add(extension, FillHorizontal)
                    }
                }
            }
        }

        EventCreateDocument.addListener {
            run()
        }

        EventOpenDocument.addListener {
            run()
        }
    }

    private fun run() {
        ConfigUtil.getSettings<AutoSaveSettings>("deflatedpickle@auto_save#*")?.let { config ->
            timer?.stop()

            timer = Timer(config.delay * MINUTE) {
                RawkyPlugin.document?.let { doc ->
                    var name = config.name

                    if (!config.replace) {
                        val count = config.path.list()?.count { it.startsWith(config.name) }

                        name = if (doc.name == null) {
                            "${config.name}-$count"
                        } else {
                            "${doc.name}-$count"
                        }
                    }

                    val file = File("${config.path.absolutePath}/$name.${config.fileType?.extension}").apply { createNewFile() }
                    config.fileType?.handler?.export(doc, file)

                    EventAutoSaveDocument.trigger(Pair(doc, file))
                }
            }.apply {
                start()
            }
        }
    }
}