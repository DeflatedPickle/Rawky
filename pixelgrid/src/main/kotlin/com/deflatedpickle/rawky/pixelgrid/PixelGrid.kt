package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.api.plugin.PluginType
import com.deflatedpickle.rawky.discordrpc.util.DiscordRP
import com.deflatedpickle.rawky.event.EventPanelFocusGained
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
    components = [PixelGridComponent::class],
    dependencies = [
        "deflatedpickle@core#1.0.0",
        "deflatedpickle@discord_rpc#1.0.0"
    ]
)
@Suppress("unused")
object PixelGrid {
    init {
        EventPanelFocusGained.addListener {
            if (it is PixelGridComponent) {
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