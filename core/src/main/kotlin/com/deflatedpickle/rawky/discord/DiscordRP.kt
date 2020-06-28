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

    val stack = object : java.util.ArrayDeque<DiscordRichPresence>() {
        override fun push(e: DiscordRichPresence) {
            DiscordRPC.discordUpdatePresence(e)
            super.push(e)
        }

        override fun pop(): DiscordRichPresence {
            super.pop()
            DiscordRPC.discordUpdatePresence(this.first)
            return this.first
        }
    }

    private const val DELAY = 1
    val timer = Timer(this.DELAY * 1000) {
        DiscordRPC.discordRunCallbacks()
    }

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