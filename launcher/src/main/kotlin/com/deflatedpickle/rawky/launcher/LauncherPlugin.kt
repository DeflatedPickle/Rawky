/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SimpleRedundantLet", "MemberVisibilityCanBePrivate")

package com.deflatedpickle.rawky.launcher

import ModernDocking.layouts.ApplicationLayoutXML
import ModernDocking.layouts.DockingLayouts
import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventImportDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.event.EventProgramShutdown
import com.deflatedpickle.haruhi.event.EventSaveDocument
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.marvin.functions.extensions.div
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.ControlMode
import com.deflatedpickle.rawky.api.impex.Exporter
import com.deflatedpickle.rawky.api.impex.Importer
import com.deflatedpickle.rawky.api.impex.Opener
import com.deflatedpickle.rawky.event.EventUpdateCell
import com.deflatedpickle.rawky.api.ImportAs
import com.deflatedpickle.rawky.launcher.gui.Window
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.undulation.functions.extensions.add
import com.deflatedpickle.rawky.launcher.gui.FilePreview
import com.deflatedpickle.undulation.widget.LimitedMenu
import org.oxbow.swingbits.dialog.task.TaskDialogs
import java.io.File
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Plugin(
    value = "launcher",
    author = "DeflatedPickle",
    version = "1.1.1",
    description = """
        <br>
        A basic launcher
    """,
    type = PluginType.LAUNCHER,
    dependencies = ["deflatedpickle@core#1.0.0"],
    settings = LauncherSettings::class,
)
@Suppress("unused")
object LauncherPlugin {
    val folder = (File(".") / "layout").apply { mkdirs() }

    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.SHORT)
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())

    private val exporterChooser =
        JFileChooser(File(".")).apply {
            isAcceptAllFileFilterUsed = false

            EventProgramFinishSetup.addListener {
                for ((k, v) in Exporter.registry) {
                    for ((nk, nv) in v.exporterExtensions) {
                        addChoosableFileFilter(
                            FileNameExtensionFilter(
                                "$nk (${nv.joinToString { "*.$it" }}) [$k]",
                                *nv.toTypedArray(),
                            ),
                        )
                    }
                }
            }
        }

    private val importerChooser =
        JFileChooser(File(".")).apply {
            accessory = FilePreview(this)

            EventProgramFinishSetup.addListener {
                for ((k, v) in Importer.registry) {
                    for ((nk, nv) in v.importerExtensions) {
                        addChoosableFileFilter(
                            FileNameExtensionFilter(
                                "$nk (${nv.joinToString { "*.$it" }}) [$k]",
                                *nv.toTypedArray(),
                            ),
                        )
                    }
                }
            }
        }

    private val openerChooser =
        JFileChooser(File(".")).apply {
            accessory = FilePreview(this)

            EventProgramFinishSetup.addListener {
                for ((k, v) in Opener.registry) {
                    for ((nk, nv) in v.openerExtensions) {
                        addChoosableFileFilter(
                            FileNameExtensionFilter(
                                "$nk (${nv.joinToString { "*.$it" }}) [$k]",
                                *nv.toTypedArray(),
                            ),
                        )
                    }
                }
            }
        }

    private val multiOpenerChooser =
        JFileChooser(File(".")).apply {
            isMultiSelectionEnabled = true

            EventProgramFinishSetup.addListener {
                for ((k, v) in Opener.registry) {
                    for ((nk, nv) in v.openerExtensions) {
                        addChoosableFileFilter(
                            FileNameExtensionFilter(
                                "$nk (${nv.joinToString { "*.$it" }}) [$k]",
                                *nv.toTypedArray(),
                            ),
                        )
                    }
                }
            }
        }

    val historyMenu = LimitedMenu("History", 6)

    init {
        EventProgramShutdown.addListener { saveLayouts() }

        EventProgramFinishSetup.addListener {
            saveLayouts()
            loadUserLayouts()

            ConfigUtil.getSettings<LauncherSettings>("deflatedpickle@launcher#*")?.let {
                historyMenu.limit = it.historyLength

                for (i in it.history) {
                    historyMenu.add("Open \"${i.absolutePath}\"") { open(i) }
                }
            }
        }

        EventCreateDocument.addListener {
            RawkyPlugin.document?.let { doc ->
                doc.dirty = false

                Window.title = "untitled - Rawky"
            }
        }

        EventOpenDocument.addListener {
            RawkyPlugin.document?.let { doc ->
                doc.dirty = false

                doc.path?.let { path ->
                    Window.title = "${path.name} - Rawky"
                }
            }
        }

        EventSaveDocument.addListener {
            RawkyPlugin.document?.let { doc ->
                doc.dirty = false

                doc.path?.let { path ->
                    Window.title = "${path.name} - Rawky"
                }
            }
        }

        EventUpdateCell.addListener {
            RawkyPlugin.document?.let { doc ->
                doc.dirty = true

                val path = doc.path

                if (path != null) {
                    Window.title = "${path.name}* - Rawky"
                } else {
                    Window.title = "untitled* - Rawky"
                }
            }
        }
    }

    fun open(file: File) {
        var none = true

        for ((_, v) in Opener.registry) {
            if (file.extension in v.openerExtensions.flatMap { it.value }) {
                none = false

                RawkyPlugin.document = v.open(file)
                    .apply { this.path = file.absoluteFile }

                val mode = RawkyPlugin.document!!.controlMode
                if (mode != null) {
                    ControlMode.current = mode
                } else {
                    ControlMode.current = ControlMode.default
                    RawkyPlugin.document!!.controlMode = ControlMode.current
                }

                ControlMode.current.apply()

                RawkyDocument.suggestedName = file.nameWithoutExtension
                RawkyDocument.suggestedExtension = file.extension

                EventOpenDocument.trigger(Pair(RawkyPlugin.document!!, file))

                break
            }
        }

        if (none) {
            TaskDialogs.error(
                Haruhi.window,
                "Invalid Openers",
                "No opener is registered for this file type",
            )
        }
    }

    fun import(file: File, importAs: ImportAs = ImportAs.LAYERS) {
        if (RawkyPlugin.document == null) return
        var none = true

        for ((_, v) in Importer.registry) {
            if (file.extension in v.importerExtensions.flatMap { it.value }) {
                none = false

                v.import(RawkyPlugin.document!!, file, importAs)
                EventImportDocument.trigger(Pair(RawkyPlugin.document!!, file))

                break
            }
        }

        if (none) {
            TaskDialogs.error(
                Haruhi.window,
                "Invalid Importers",
                "No importer is registered for this file type",
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
                    doc.path = file.absoluteFile
                    EventSaveDocument.trigger(Pair(doc, file))
                } else {
                    TaskDialogs.error(Haruhi.window, "Invalid Document", "No document exists to export")
                }
                break
            }
        }

        if (none) {
            TaskDialogs.error(
                Haruhi.window,
                "Invalid Exporters",
                "No exporter is registered for this file type",
            )
        }
    }

    fun openDialog() {
        if (openerChooser.showOpenDialog(Haruhi.window) == JFileChooser.APPROVE_OPTION) {
            open(openerChooser.selectedFile)
            addToHistory(openerChooser.selectedFile)
        }
    }

    fun multiOpenDialog(importAs: ImportAs) {
        if (multiOpenerChooser.showOpenDialog(Haruhi.window) == JFileChooser.APPROVE_OPTION) {
            open(multiOpenerChooser.selectedFiles.first())

            RawkyPlugin.document?.let {
                for (i in multiOpenerChooser.selectedFiles.drop(1)) {
                    import(i, importAs)

                    when (importAs) {
                        ImportAs.FRAMES -> it.selectedIndex++
                        ImportAs.LAYERS -> it[it.selectedIndex].selectedIndex++
                    }
                }
            }
        }
    }

    fun importDialog() {
        if (importerChooser.showOpenDialog(Haruhi.window) == JFileChooser.APPROVE_OPTION) {
            import(importerChooser.selectedFile)
        }
    }

    fun exportDialog() {
        RawkyDocument.suggestedName?.let { name ->
            exporterChooser.selectedFile = File(".") / "${name}.${RawkyDocument.suggestedExtension ?: "rawr"}"
        }

        if (exporterChooser.showSaveDialog(Haruhi.window) == JFileChooser.APPROVE_OPTION) {
            var file = exporterChooser.selectedFile
            if (file.extension == "") {
                file =
                    File(
                        "${file.absolutePath}.${(exporterChooser.fileFilter as FileNameExtensionFilter).extensions.first()}",
                    )
            }

            if (file.exists() && !TaskDialogs.ask(
                    Haruhi.window,
                    "Overwrite?",
                    "The file \"${file.absolutePath}\" already exists. Would you like to overwrite it?"
            )) {
                return
            }

            export(file)
            addToHistory(file)
        }
    }

    fun addToHistory(file: File) {
        ConfigUtil.getSettings<LauncherSettings>("deflatedpickle@launcher#*")?.let {
            if (!it.history.contains(file)) {
                it.history.add(file)
            }

            if (it.history.size >= it.historyLength) {
                for (i in it.historyLength until it.history.size) {
                    it.history.removeAt(0)
                }
            }

            historyMenu.add("Open \"${file.absolutePath}\"")

            PluginUtil.slugToPlugin("deflatedpickle@launcher#*")?.let { plug ->
                ConfigUtil.serializeConfig(plug)
            }
        }
    }

    fun saveLayouts() {
        for (l in DockingLayouts.getLayoutNames()) {
            ApplicationLayoutXML.saveLayoutToFile(folder / "$l.xml", DockingLayouts.getLayout(l))
        }
    }

    fun loadUserLayouts() {
        for (f in folder.walk()) {
            if (f.isFile &&
                f.extension == "xml" &&
                DockingLayouts.getLayoutNames().indexOf(f.nameWithoutExtension) == -1
            ) {
                DockingLayouts.addLayout(f.name, ApplicationLayoutXML.loadLayoutFromFile(f))
            }
        }
    }
}
