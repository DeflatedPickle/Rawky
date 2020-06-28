package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.api.plugin.PluginType
import com.deflatedpickle.rawky.discord.DiscordRP
import com.deflatedpickle.rawky.event.EventPanelFocusGained
import com.deflatedpickle.rawky.event.EventWindowDeployed
import net.arikia.dev.drpc.DiscordRPC
import net.arikia.dev.drpc.DiscordRichPresence

@Plugin(
    value = "pixel_grid",
    author = "DeflatedPickle",
    description = """
        Provides a grid to draw upon
        <br>
        This plugin provides most of the basic user functionality of Rawky.
    """,
    type = PluginType.COMPONENT,
    components = [PixelGridComponent::class]
)
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