/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SimpleRedundantLet", "MemberVisibilityCanBePrivate")

package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventImportDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.event.EventSaveDocument
import com.deflatedpickle.haruhi.util.ConfigUtil
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
import com.deflatedpickle.undulation.widget.LimitedMenu
import org.oxbow.swingbits.dialog.task.TaskDialogs
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JMenu
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.system.exitProcess

@Plugin(
    value = "launcher",
    author = "DeflatedPickle",
    version = "1.1.1",
    description = """
        <br>
        A basic launcher
    """,
    type = PluginType.LAUNCHER,
    dependencies = [
        "deflatedpickle@core#1.0.0"
    ],
    settings = LauncherSettings::class,
)
@Suppress("unused")
object LauncherPlugin {
    private val exporterChooser = JFileChooser(File(".")).apply {
        EventProgramFinishSetup.addListener {
            for ((k, v) in Exporter.registry) {
                for ((nk, nv) in v.exporterExtensions) {
                    addChoosableFileFilter(
                        FileNameExtensionFilter(
                            "$nk (${nv.joinToString { "*.$it" }}) [${k}]",
                            *nv.toTypedArray()
                        )
                    )
                }
            }
        }
    }

    private val importerChooser = JFileChooser(File(".")).apply {
        EventProgramFinishSetup.addListener {
            for ((k, v) in Importer.registry) {
                for ((nk, nv) in v.importerExtensions) {
                    addChoosableFileFilter(
                        FileNameExtensionFilter(
                            "$nk (${nv.joinToString { "*.$it" }}) [${k}]",
                            *nv.toTypedArray()
                        )
                    )
                }
            }
        }
    }

    private val openerChooser = JFileChooser(File(".")).apply {
        EventProgramFinishSetup.addListener {
            for ((k, v) in Opener.registry) {
                for ((nk, nv) in v.openerExtensions) {
                    addChoosableFileFilter(
                        FileNameExtensionFilter(
                            "$nk (${nv.joinToString { "*.$it" }}) [${k}]",
                            *nv.toTypedArray()
                        )
                    )
                }
            }
        }
    }

    private val historyMenu = LimitedMenu("History", 0).apply {
        ConfigUtil.getSettings<LauncherSettings>("deflatedpickle@launcher#*")?.let {
            this.limit = it.historyLength

            for (i in it.history) {
                add("Open \"${i.absolutePath}\"") {
                    open(i)
                }
            }
        }
    }

    init {
        EventProgramFinishSetup.addListener {
            val menuBar = RegistryUtil.get(MenuCategory.MENU.name)
            (menuBar?.get(MenuCategory.FILE.name) as JMenu).apply {
                add("New", MonoIcon.FOLDER_NEW) { ActionUtil.newFile() }

                add("Open", MonoIcon.FOLDER_OPEN) {
                    openDialog(this)
                }

                ConfigUtil.getSettings<LauncherSettings>("deflatedpickle@launcher#*")?.let {
                    if (it.history.isNotEmpty()) {
                        add(historyMenu)
                    }
                }

                addSeparator()

                add("Import", MonoIcon.FILE_NEW) { importDialog() }
                add("Export", MonoIcon.FILE_EXPORT) { exportDialog() }

                addSeparator()

                add("Exit", MonoIcon.EXIT) { exitProcess(0) }

                addSeparator()
            }

            Toolbar.apply {
                add(icon = MonoIcon.FOLDER_NEW, tooltip = "New") { ActionUtil.newFile() }
                add(icon = MonoIcon.FOLDER_OPEN, tooltip = "Open") {
                    val fileMenu = (menuBar.get(MenuCategory.FILE.name) as JMenu)
                    openDialog(fileMenu)
                }

                addSeparator()

                add(icon = MonoIcon.FILE_NEW, tooltip = "Import") { importDialog() }
                add(icon = MonoIcon.FILE_EXPORT, tooltip = "Export") { exportDialog() }
            }
        }
    }

    fun open(file: File) {
        var none = true

        for ((_, v) in Opener.registry) {
            if (file.extension in v.openerExtensions.flatMap { it.value }) {
                none = false
                RawkyPlugin.document = v.open(file).apply {
                    this.name = file.nameWithoutExtension
                }
                EventOpenDocument.trigger(Pair(RawkyPlugin.document!!, file))

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

    fun import(file: File) {
        var none = true

        for ((_, v) in Importer.registry) {
            if (file.extension in v.importerExtensions.flatMap { it.value }) {
                none = false

                v.import(
                    RawkyPlugin.document ?: ActionUtil.newDocument(
                        16, 16,
                        1, 1
                    ).apply { RawkyPlugin.document = this },
                    file
                )
                EventImportDocument.trigger(Pair(RawkyPlugin.document!!, file))

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

    fun export(file: File) {
        var none = true

        for ((_, v) in Exporter.registry) {
            if (file.extension in v.exporterExtensions.flatMap { it.value }) {
                none = false
                val doc = RawkyPlugin.document

                if (doc != null) {
                    v.export(doc, file)
                    EventSaveDocument.trigger(Pair(doc, file))
                    doc.name = file.nameWithoutExtension
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

    fun openDialog(menu: JMenu) {
        if (openerChooser.showOpenDialog(PluginUtil.window) == JFileChooser.APPROVE_OPTION) {
            open(openerChooser.selectedFile)

            if (!menu.menuComponents.contains(historyMenu)) {
                menu.add(historyMenu)
            }

            ConfigUtil.getSettings<LauncherSettings>("deflatedpickle@launcher#*")?.let {
                it.history.add(openerChooser.selectedFile)

                if (it.history.size >= it.historyLength) {
                    for (i in it.historyLength until it.history.size) {
                        it.history.removeAt(0)
                    }
                }

                historyMenu.add("Open \"${openerChooser.selectedFile.absolutePath}\"")

                PluginUtil.slugToPlugin("deflatedpickle@launcher#*")
                    ?.let { plug -> ConfigUtil.serializeConfig(plug) }
            }
        }
    }

    fun importDialog() {
        if (importerChooser.showOpenDialog(PluginUtil.window) == JFileChooser.APPROVE_OPTION) {
            import(importerChooser.selectedFile)
        }
    }

    fun exportDialog() {
        if (exporterChooser.showSaveDialog(PluginUtil.window) == JFileChooser.APPROVE_OPTION) {
            var file = exporterChooser.selectedFile
            if (file.extension == "") {
                file = File("${file.absolutePath}.${(exporterChooser.fileFilter as FileNameExtensionFilter).extensions.first()}")
            }

            export(file)
        }
    }
}
