/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection", "MemberVisibilityCanBePrivate")

package com.deflatedpickle.rawky

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.event.EventSerializeConfig
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.marvin.functions.extensions.div
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.api.template.Guide
import com.deflatedpickle.rawky.event.EventChangeTool
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.setting.RawkySettings
import com.deflatedpickle.undulation.api.MenuButtonType.CHECK
import com.deflatedpickle.undulation.functions.extensions.add
import kotlinx.serialization.json.Json
import so.jabber.FileUtils
import java.io.File
import javax.swing.JCheckBoxMenuItem
import javax.swing.JMenu
import javax.swing.KeyStroke

@Plugin(
    value = "core",
    author = "DeflatedPickle",
    version = "1.0.0",
    description =
    """
        The core program
        <br>
        This provides the main API for Rawky
    """,
    type = PluginType.CORE_API,
    settings = RawkySettings::class,
)
object RawkyPlugin {
    val templateFolder = (File(".") / "template").apply { mkdirs() }
    private val guideFolder = (File(".") / "guide").apply { mkdirs() }

    var rows = -1
    var columns = -1

    // TODO: support multiple documents
    var document: RawkyDocument? = null

    init {
        EventSerializeConfig.addListener {
            if ("core" in it.name) {
                if (Tool.isToolValid()) {
                    EventChangeTool.trigger(Tool.current)
                }
            }
        }

        FileUtils.copyResourcesRecursively(
            RawkyPlugin::class.java.getResource("/template"),
            templateFolder,
        )

        FileUtils.copyResourcesRecursively(RawkyPlugin::class.java.getResource("/guide"), guideFolder)

        for (i in guideFolder.walk()) {
            if (i.isFile && i.extension == "json") {
                val json = Json.Default.decodeFromString<List<Guide>>(i.readText())
                Guide.registry[i.nameWithoutExtension] = json
            }
        }

        EventProgramFinishSetup.addListener {
            RegistryUtil.get(MenuCategory.MENU.name)?.apply {
                (get(MenuCategory.TOOLS.name) as JMenu).apply {
                    add(
                        "Debug",
                        MonoIcon.WRENCH,
                        KeyStroke.getKeyStroke("F3"),
                        "Toggle debugging hints",
                        type = CHECK,
                    ) { a ->
                        Haruhi.isInDev = (a.source as JCheckBoxMenuItem).isSelected

                        document?.let { doc ->
                            val frame = doc.children[doc.selectedIndex]
                            val layer = frame.children[frame.selectedIndex]
                            val grid = layer.child

                            EventUpdateGrid.trigger(grid)
                        }
                    }.apply {
                        isSelected = Haruhi.isInDev
                    }

                    addSeparator()
                }
            }
        }
    }
}
