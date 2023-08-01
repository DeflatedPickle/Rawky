/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection", "UNCHECKED_CAST")

package com.deflatedpickle.rawky.autosave

import com.deflatedpickle.haruhi.Haruhi
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
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.undulation.constraints.FillHorizontal
import java.awt.Color
import java.awt.Component
import java.awt.GridBagLayout
import java.awt.Panel
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.Timer

@Plugin(
    value = "auto_save",
    author = "DeflatedPickle",
    version = "1.2.0",
    description =
    """
        <br>
        Adds a timer to automatically save the current file
    """,
    dependencies =
    [
        "deflatedpickle@core#*",
    ],
    settings = AutoSaveSettings::class,
)
@Suppress("unused")
object AutoSavePlugin {
    // Miliseconds in a minute
    private const val MINUTE = 60000

    private var timer: Timer? = null

    init {
        object : WindowAdapter() {
            override fun windowLostFocus(e: WindowEvent) {
                ConfigUtil.getSettings<AutoSaveSettings>("deflatedpickle@auto_save#*")?.let { config ->
                    RawkyPlugin.document?.let { doc ->
                        if (e.oppositeWindow !in Haruhi.window.ownedWindows &&
                            e.oppositeWindow != null &&
                            e.oppositeWindow.owner == Haruhi.window
                        ) {
                            if (config.saveOnFocusLost) {
                                save(doc, config)
                            }
                        }
                    }
                }
            }
        }
            .also {
                with(Haruhi.window) {
                    addWindowListener(it)
                    addWindowFocusListener(it)
                    addWindowStateListener(it)
                }
            }

        EventProgramFinishSetup.addListener {
            ConfigUtil.getSettings<AutoSaveSettings>("deflatedpickle@auto_save#*")?.let { config ->
                if (config.fileType == null) {
                    val exporter = Exporter.registry.values.first()
                    config.fileType = FileType(exporter, exporter.exporterExtensions.values.first().first())

                    PluginUtil.slugToPlugin("deflatedpickle@auto_save#*")?.let { plug ->
                        ConfigUtil.serializeConfig(plug)
                    }
                }
            }

            (RegistryUtil.get("setting_type") as Registry<String, (Plugin, String, Any) -> Component>?)
                ?.let { registry ->
                    registry.register(FileType::class.qualifiedName!!) { plugin, name, instance ->
                        Panel(GridBagLayout()).apply {
                            val inst = instance.get<FileType>(name)

                            val handler =
                                JComboBox(Exporter.registry.values.toTypedArray()).apply {
                                    selectedItem = inst.handler

                                    setRenderer { _, value, _, _, _ -> JLabel((value as Exporter).name) }
                                }
                            val extension =
                                JComboBox(
                                    (handler.selectedItem as Exporter)
                                        .exporterExtensions
                                        .flatMap { it.value }
                                        .toTypedArray(),
                                )
                                    .apply { selectedItem = inst.extension }

                            handler.addItemListener {
                                (extension.model as DefaultComboBoxModel).apply {
                                    removeAllElements()
                                    addAll(
                                        (handler.selectedItem as Exporter).exporterExtensions.flatMap { it.value },
                                    )
                                }
                                extension.selectedIndex = 0

                                instance.set(
                                    name,
                                    FileType(
                                        handler.selectedItem as Exporter,
                                        extension.selectedItem as String? ?: "null",
                                    ),
                                )
                                ConfigUtil.serializeConfig(plugin)
                            }

                            extension.addItemListener {
                                instance.set(
                                    name,
                                    FileType(
                                        handler.selectedItem as Exporter,
                                        extension.selectedItem as String? ?: "null",
                                    ),
                                )
                                ConfigUtil.serializeConfig(plugin)
                            }

                            add(handler, FillHorizontal)
                            add(extension, FillHorizontal)
                        }
                    }
                }
        }

        EventCreateDocument.addListener { run() }

        EventOpenDocument.addListener { run() }
    }

    private fun run() {
        ConfigUtil.getSettings<AutoSaveSettings>("deflatedpickle@auto_save#*")?.let { config ->
            timer?.stop()

            timer =
                Timer(config.delay * MINUTE) { RawkyPlugin.document?.let { doc -> save(doc, config) } }
                    .apply { start() }
        }
    }

    fun save(doc: RawkyDocument, config: AutoSaveSettings) {
        if (config.ignoreEmpty && checkEmpty(doc)) {
            return
        }

        var name = config.name

        if (!config.replace) {
            val count = config.path.list()?.count { it.startsWith(config.name) }

            name =
                if (doc.path == null ||
                    doc.path!!.nameWithoutExtension
                        .contains(config.name)
                ) {
                    "${config.name}-$count"
                } else {
                    "${doc.path}-$count"
                }
        }

        val file =
            File("${config.path.absolutePath}/$name.${config.fileType?.extension}").apply {
                createNewFile()
            }
        config.fileType?.handler?.export(doc, file)

        EventAutoSaveDocument.trigger(Pair(doc, file))
    }

    private fun checkEmpty(doc: RawkyDocument): Boolean {
        val transparent = Color(0, 0, 0, 0)
        var colour = transparent

        for (f in doc.children) {
            for (l in f.children) {
                for (c in l.child.children) {
                    // TODO
          /*if (c.colour != transparent) {
              colour = c.colour
          }*/
                }
            }
        }

        return colour == transparent
    }
}
