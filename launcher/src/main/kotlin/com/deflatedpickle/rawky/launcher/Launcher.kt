package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.api.plugin.PluginType
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.collection.Frame
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.collection.Layer
import com.deflatedpickle.rawky.event.reusable.EventMenuBuild
import com.deflatedpickle.rawky.event.specific.EventCreateRawkyDocument
import com.deflatedpickle.rawky.launcher.config.LauncherSettings
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.ui.dialog.NewFileDialog
import com.deflatedpickle.rawky.ui.extension.addItem
import com.deflatedpickle.rawky.ui.menu.MenuFile
import com.deflatedpickle.rawky.util.DocumentUtil
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Rectangle

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
    settings = LauncherSettings::class
)
@Suppress("unused")
object Launcher {
    init {
        EventMenuBuild.addListener {
            if (it is MenuFile) {
                it.addItem("New") {
                    NewFileDialog.isVisible = true

                    if (NewFileDialog.result == TaskDialog.StandardCommand.OK) {
                        val maxRows = NewFileDialog.rowInput.text.toInt()
                        val maxColumns = NewFileDialog.columnInput.text.toInt()

                        val document = RawkyDocument(
                            children = Array(NewFileDialog.framesInput.text.toInt()) {
                                Frame(
                                    Array(NewFileDialog.layersInput.text.toInt()) {
                                        Layer(
                                            Grid(
                                                rows = maxRows,
                                                columns = maxColumns
                                            )
                                        )
                                    }
                                )
                            }
                        )

                        DocumentUtil.document = document

                        EventCreateRawkyDocument.trigger(document)
                    }
                }
            }
        }
    }
}