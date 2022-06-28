package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.impex.Exporter
import com.deflatedpickle.rawky.api.impex.Importer
import com.deflatedpickle.rawky.api.impex.Opener
import com.deflatedpickle.rawky.launcher.gui.Toolbar
import com.deflatedpickle.rawky.util.ActionUtil
import com.deflatedpickle.undulation.functions.extensions.add
import org.oxbow.swingbits.dialog.task.TaskDialogs
import javax.swing.JFileChooser
import javax.swing.JMenu
import javax.swing.filechooser.FileNameExtensionFilter

@Plugin(
    value = "launcher",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A basic launcher
    """,
    type = PluginType.LAUNCHER,
    dependencies = [
        "deflatedpickle@core#1.0.0"
    ],
)
@Suppress("unused")
object LauncherPlugin {
    private val chooser = JFileChooser().apply {
        EventProgramFinishSetup.addListener {
            for ((k, v) in Exporter.registry) {
                addChoosableFileFilter(
                    FileNameExtensionFilter(
                        "$k (${v.extensions.joinToString { "*.$it" }})",
                        *v.extensions.toTypedArray()
                    )
                )
            }
        }
    }

    init {
        EventProgramFinishSetup.addListener {
            val menuBar = RegistryUtil.get(MenuCategory.MENU.name)
            (menuBar?.get(MenuCategory.FILE.name) as JMenu).apply {
                add("New", MonoIcon.FOLDER_NEW) { ActionUtil.newFile() }

                add("Open", MonoIcon.FOLDER_OPEN) {
                    if (chooser.showSaveDialog(PluginUtil.window) == JFileChooser.APPROVE_OPTION) {
                        var none = true

                        for ((_, v) in Opener.registry) {
                            if (chooser.selectedFile.extension in v.extensions) {
                                none = false
                                RawkyPlugin.document = v.open(chooser.selectedFile)
                                EventCreateDocument.trigger(RawkyPlugin.document!!)

                                break
                            }
                        }

                        if (none) {
                            TaskDialogs.error(
                                PluginUtil.window,
                                "Invalid Openers",
                                "No opener is registered for this file type"
                            )
                        }
                    }
                }

                addSeparator()

                add("Import") {
                    if (chooser.showSaveDialog(PluginUtil.window) == JFileChooser.APPROVE_OPTION) {
                        var none = true

                        for ((_, v) in Importer.registry) {
                            if (chooser.selectedFile.extension in v.extensions) {
                                none = false

                                v.import(
                                    RawkyPlugin.document ?: ActionUtil.newDocument(
                                        16, 16,
                                        1, 1
                                    ).apply { RawkyPlugin.document = this },
                                    chooser.selectedFile
                                )
                                EventCreateDocument.trigger(RawkyPlugin.document!!)

                                break
                            }
                        }

                        if (none) {
                            TaskDialogs.error(
                                PluginUtil.window,
                                "Invalid Importers",
                                "No importer is registered for this file type"
                            )
                        }
                    }
                }

                add("Export") {
                    if (chooser.showSaveDialog(PluginUtil.window) == JFileChooser.APPROVE_OPTION) {
                        var none = true

                        for ((_, v) in Exporter.registry) {
                            if (chooser.selectedFile.extension in v.extensions) {
                                none = false
                                val doc = RawkyPlugin.document

                                if (doc != null) {
                                    v.export(doc, chooser.selectedFile)
                                } else {
                                    TaskDialogs.error(
                                        PluginUtil.window,
                                        "Invalid Document",
                                        "No document exists to export"
                                    )
                                }
                                break
                            }
                        }

                        if (none) {
                            TaskDialogs.error(
                                PluginUtil.window,
                                "Invalid Exporters",
                                "No exporter is registered for this file type"
                            )
                        }
                    }
                }

                addSeparator()
            }

            Toolbar.apply {
                add(icon = MonoIcon.FOLDER_NEW, tooltip = "New") { ActionUtil.newFile() }
                // add(icon = MonoIcon.FOLDER_OPEN, tooltip = "Open Pack") { ActionUtil.openPack() }
            }
        }
    }
}