@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourhistory

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.api.util.ComponentPosition
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively
import com.deflatedpickle.undulation.widget.ColourButton
import java.awt.Color

@Plugin(
    value = "colour_history",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Shows your previously selected colours
    """,
    type = PluginType.COMPONENT,
    component = ColourHistoryPanel::class,
    componentVisible = false,
    componentMinimizedPosition = ComponentPosition.WEST,
    settings = ColourHistorySettings::class,
)
@Suppress("unused")
object ColourHistoryPlugin {
    init {
        EventChangeTheme.addListener {
            ColourHistoryPanel.updateUIRecursively()
        }

        EventProgramFinishSetup.addListener {
            ConfigUtil.getSettings<ColourHistorySettings>("deflatedpickle@colour_history#*")?.let {
                for (i in it.history) {
                    addButton(i)
                }
            }
        }

        EventChangeColour.addListener { c ->
            if (ColourHistoryPanel.components.none { b -> (b as ColourButton).color == c }) {
                addButton(c)

                ConfigUtil.getSettings<ColourHistorySettings>("deflatedpickle@colour_history#*")?.let {
                    updateHistory(it, c)
                    ColourHistoryPanel.remove(ColourHistoryPanel.components.lastIndex)
                }

                PluginUtil.slugToPlugin("deflatedpickle@colour_history#*")
                    ?.let { plug -> ConfigUtil.serializeConfig(plug) }
            }

            ColourHistoryPanel.repaint()
        }
    }

    private fun addButton(c: Color) {
        ColourHistoryPanel.add(ColourButton(c).apply {
            addActionListener {
                RawkyPlugin.colour = this.color
            }
        }, 0)
    }

    private fun updateHistory(it: ColourHistorySettings, c: Color) {
        it.history.add(c)

        if (it.history.size >= it.historyLength) {
            for (i in it.historyLength until it.history.size) {
                it.history.removeAt(0)
            }
        }
    }
}