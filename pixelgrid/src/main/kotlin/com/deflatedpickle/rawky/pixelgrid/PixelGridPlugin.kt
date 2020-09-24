package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventPanelFocusGained
import com.deflatedpickle.rawky.discordrpc.util.DiscordRP
import net.arikia.dev.drpc.DiscordRichPresence

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
        "deflatedpickle@discord_rpc#*"
    ]
)
@Suppress("unused")
object PixelGridPlugin {
    init {
        EventPanelFocusGained.addListener {
            if (it is PixelGridPanel) {
                DiscordRP.stack.push(
                    DiscordRichPresence
                        .Builder("Pixel Grid")
                        .setDetails("Editing: null")
                        .setStartTimestamps(System.currentTimeMillis())
                        .build()
                )
            }
        }
    }
}