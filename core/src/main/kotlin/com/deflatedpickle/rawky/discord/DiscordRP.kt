package com.deflatedpickle.rawky.discord

import net.arikia.dev.drpc.DiscordEventHandlers
import net.arikia.dev.drpc.DiscordRPC
import net.arikia.dev.drpc.DiscordRichPresence
import org.apache.logging.log4j.LogManager
import javax.swing.Timer

object DiscordRP {
    private val logger = LogManager.getLogger(this::class.simpleName)

    private const val ID = "726574754919874560"
    private val handlers = DiscordEventHandlers.Builder()
        .setReadyEventHandler {
            logger.info("Discord RCP started for user ${it.username}#${it.discriminator}")
        }
        .setErroredEventHandler { errorCode, message ->
            logger.error("Discord RCP threw an error; $errorCode, $message")
        }
        .build()

    /**
     * Initializes the RCP connection
     */
    fun initializeRCP() {
        DiscordRPC.discordInitialize(
            this.ID,
            handlers,
            true
        )
    }

    /**
     * Shuts down the RCP connection
     */
    fun shutdownRCP() {
        DiscordRPC.discordShutdown()
    }
}