package com.deflatedpickle.rawky.discordrpc.util

import com.deflatedpickle.rawky.ui.window.Window
import com.deflatedpickle.tosuto.ToastItem
import com.deflatedpickle.tosuto.action.ToastSingleAction
import com.deflatedpickle.tosuto.api.ToastLevel
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

            Window.toastWindow.addToast(
                ToastItem(
                    title = "Discord RPC",
                    content = "Connected to the account ${it.username}#${it.discriminator}",
                    actions = listOf(
                        ToastSingleAction(
                            "Disconnect",
                            command = { _, toast ->
                                shutdownRCP()
                                toast.close()
                            }
                        )
                    )
                )
            )
        }
        .setErroredEventHandler { errorCode, message ->
            logger.error("Discord RCP threw an error; $errorCode, $message")

            Window.toastWindow.addToast(
                ToastItem(
                    level = ToastLevel.ERROR,
                    title = "Discord RPC",
                    content = "Threw an error; $message (code $errorCode)"
                )
            )
        }
        .setDisconnectedEventHandler { errorCode, message ->
            logger.warn("Discord RCP was disconnected; $message, with the error code $errorCode")

            Window.toastWindow.addToast(
                ToastItem(
                    level = ToastLevel.WARNING,
                    title = "Discord RPC",
                    content = "Disconnected; $message (code $errorCode)"
                )
            )
        }
        .build()

    // A special queue that updates the users presence with each push/pop
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