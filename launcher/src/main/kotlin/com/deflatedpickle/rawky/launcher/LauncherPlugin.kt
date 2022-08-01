@file:Suppress("SimpleRedundantLet")

package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
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
    settings = LauncherSettings::class,
)
@Suppress("unused")
object LauncherPlugin {
    private val chooser = JFileChooser(File(".")).apply {
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
                val historyMenu = LimitedMenu("History", 0).apply {
                    ConfigUtil.getSettings<LauncherSettings>("deflatedpickle@launcher#*")?.let {
                        this.limit = it.historyLength

                        for (i in it.history) {
                            add("Open \"${i.absolutePath}\"") {
                                open(i)
                            }
                        }
                    }
                }

                add("New", MonoIcon.FOLDER_NEW) { ActionUtil.newFile() }

                add("Open", MonoIcon.FOLDER_OPEN) {
                    if (chooser.showOpenDialog(PluginUtil.window) == JFileChooser.APPROVE_OPTION) {
                        open(chooser.selectedFile)

                        if (!this.menuComponents.contains(historyMenu)) {
                            add(historyMenu)
                        }

                        ConfigUtil.getSettings<LauncherSettings>("deflatedpickle@launcher#*")?.let {
                            it.history.add(chooser.selectedFile)

                            if (it.history.size >= it.historyLength) {
                                for (i in it.historyLength until it.history.size) {
                                    it.history.removeAt(0)
                                }
                            }

                            historyMenu.add("Open \"${chooser.selectedFile.absolutePath}\"")

                            PluginUtil.slugToPlugin("deflatedpickle@launcher#*")
                                ?.let { plug -> ConfigUtil.serializeConfig(plug) }
                        }
                    }
                }

                ConfigUtil.getSettings<LauncherSettings>("deflatedpickle@launcher#*")?.let {
                    if (it.history.isNotEmpty()) {
                        add(historyMenu)
                    }
                }

                addSeparator()

                add("Import", MonoIcon.FILE_NEW) {
                    if (chooser.showOpenDialog(PluginUtil.window) == JFileChooser.APPROVE_OPTION) {
                        import(chooser.selectedFile)
                    }
                }

                add("Export", MonoIcon.FILE_EXPORT) {
                    if (chooser.showSaveDialog(PluginUtil.window) == JFileChooser.APPROVE_OPTION) {
                        export(chooser.selectedFile)
                    }
                }

                addSeparator()
            }

            Toolbar.apply {
                add(icon = MonoIcon.FOLDER_NEW, tooltip = "New") { ActionUtil.newFile() }
            }
        }
    }

    fun open(file: File) {
        var none = true

        for ((_, v) in Opener.registry) {
            if (file.extension in v.extensions) {
                none = false
                RawkyPlugin.document = v.open(file)
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

    fun import(file: File) {
        var none = true

        for ((_, v) in Importer.registry) {
            if (file.extension in v.extensions) {
                none = false

                v.import(
                    RawkyPlugin.document ?: ActionUtil.newDocument(
                        16, 16,
                        1, 1
                    ).apply { RawkyPlugin.document = this },
                    file
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

    fun export(file: File) {
        var none = true

        for ((_, v) in Exporter.registry) {
            if (file.extension in v.extensions) {
                none = false
                val doc = RawkyPlugin.document

                if (doc != null) {
                    v.export(doc, file)
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