package com.deflatedpickle.rawky.discordrpc

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.api.plugin.PluginType
import com.deflatedpickle.rawky.discordrpc.util.DiscordRP
import com.deflatedpickle.rawky.event.EventRawkyInit
import com.deflatedpickle.rawky.event.EventWindowShown
import com.deflatedpickle.rawky.ui.window.Window
import com.deflatedpickle.rawky.util.ConfigUtil
import net.arikia.dev.drpc.DiscordRichPresence

@Plugin(
    value = "discord_rpc",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Adds Discord RCP integration
    """,
    type = PluginType.API,
    dependencies = [
        "deflatedpickle@core#1.0.0"
    ],
    settings = DiscordRPCSettings::class
)
@Suppress("unused")
object DiscordRPC {
    init {
        EventRawkyInit.addListener {
            val settings = ConfigUtil.getSettings<DiscordRPCSettings>(
                "deflatedpickle@discord_rpc#1.0.0"
            )
            val enabled = settings.enabled

            if (enabled) {
                // Connect to Discord RCP
                this.start()
            }

            // Add a shutdown hook
            Runtime.getRuntime().addShutdownHook(object : Thread() {
                override fun run() {
                    if (enabled) {
                        // Shutdown Discord RCP
                        this@DiscordRPC.stop()
                    }
                }
            })

            if (enabled) {
                EventWindowShown.addListener {
                    if (it is Window) {
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
        }
    }

    private fun start() {
        DiscordRP.initializeRCP()
        DiscordRP.timer.start()
    }

    private fun stop() {
        DiscordRP.shutdownRCP()
        DiscordRP.timer.stop()
    }
}