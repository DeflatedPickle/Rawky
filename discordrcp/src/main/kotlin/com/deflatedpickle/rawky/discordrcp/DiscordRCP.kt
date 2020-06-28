package com.deflatedpickle.rawky.discordrcp

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.api.plugin.PluginType
import com.deflatedpickle.rawky.discordrcp.util.DiscordRP
import com.deflatedpickle.rawky.event.EventWindowShown
import net.arikia.dev.drpc.DiscordRichPresence

@Plugin(
    value = "discord_rcp",
    author = "DeflatedPickle",
    description = """
        <br>
        Adds Discord RCP integration
    """,
    types = [PluginType.API],
    dependencies = ["core"]
)
object DiscordRCP {
    init {
        // Connect to Discord RCP
        DiscordRP.initializeRCP()
        DiscordRP.timer.start()

        // Add a shutdown hook
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                // Shutdown Discord RCP
                DiscordRP.shutdownRCP()
                DiscordRP.timer.stop()
            }
        })

        EventWindowShown.addListener {
            DiscordRP.stack.push(
                DiscordRichPresence
                    .Builder("")
                    .setDetails("Hanging around, doing nothing")
                    .setStartTimestamps(System.currentTimeMillis())
                    .build()
            )
        }
    }
}