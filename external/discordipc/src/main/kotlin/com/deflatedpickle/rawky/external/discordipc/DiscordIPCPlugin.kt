@file:OptIn(DelicateCoroutinesApi::class)

package com.deflatedpickle.rawky.external.discordipc

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.event.EventProgramShutdown
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.event.EventChangeFrame
import com.deflatedpickle.rawky.event.EventChangeLayer
import com.deflatedpickle.rawky.event.EventChangeTool
import com.deflatedpickle.rawky.setting.RawkyDocument
import dev.cbyrne.kdiscordipc.KDiscordIPC
import dev.cbyrne.kdiscordipc.core.event.impl.CurrentUserUpdateEvent
import dev.cbyrne.kdiscordipc.core.event.impl.ErrorEvent
import dev.cbyrne.kdiscordipc.core.event.impl.ReadyEvent
import dev.cbyrne.kdiscordipc.core.event.impl.internal.DisconnectedEvent
import dev.cbyrne.kdiscordipc.data.activity.largeImage
import dev.cbyrne.kdiscordipc.data.activity.smallImage
import dev.cbyrne.kdiscordipc.data.activity.timestamps
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

@Plugin(
    value = "discord_ipc",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Discord Rich Presence support
    """,
    type = PluginType.OTHER,
    dependencies = ["deflatedpickle@core#1.0.0"],
)
@Suppress("unused")
object DiscordIPCPlugin {
    const val ID = "726574754919874560"
    private val ipc = KDiscordIPC(ID)

    init {
        ipc.scope.launch {
            ipc.on<ReadyEvent> {
                ipc.activityManager.setActivity {
                    state = "Launching"
                    largeImage("icon")
                    timestamps(System.currentTimeMillis())
                }
            }

            ipc.on<ErrorEvent> {
                println("error: $this")
            }

            ipc.on<DisconnectedEvent> {
                println("disconnected: $this")
            }

            ipc.on<CurrentUserUpdateEvent> {
                println("update: $this")
            }

            ipc.connect()
        }

        EventProgramFinishSetup.addListener {
            ipc.scope.launch {
                ipc.activityManager.setActivity {
                    state = "Idle"
                    largeImage("icon")
                    timestamps(System.currentTimeMillis())
                }
            }
        }

        EventCreateDocument.addListener {
            update()
        }

        EventOpenDocument.addListener {
            update()
        }

        EventChangeFrame.addListener {
            update()
        }

        EventChangeLayer.addListener {
            update()
        }

        EventChangeTool.addListener {
            update()
        }

        EventProgramShutdown.addListener {
            ipc.scope.launch {
                ipc.disconnect()
            }
        }
    }

    private fun document() = RawkyPlugin.document.let { doc ->
        if (doc == null) {
            "untitled"
        } else {
            doc.path?.name ?: ((RawkyDocument.suggestedName ?: "untitled")
                    + if (RawkyDocument.suggestedExtension != null)
                        ".${RawkyDocument.suggestedExtension}" else "")
        }
    }

    private fun update() {
        ipc.scope.launch {
            ipc.activityManager.setActivity {
                details = "Editing ${document()}"
                val doc = RawkyPlugin.document!!
                if (doc.selectedIndex < 0 || doc.selectedIndex >= doc.children.size) return@setActivity
                val frame = doc.children[doc.selectedIndex]
                val frameIndex = doc.selectedIndex
                val frameCount = doc.children.count()
                val layerIndex = frame.selectedIndex
                val layerCount = frame.children.count()
                state = "Frame ${frameIndex + 1}/${frameCount}, Layer ${layerIndex + 1}/${layerCount}"

                largeImage("icon", "${doc.rows}x${doc.columns} (${doc.cellProvider?.name})")

                if (Tool.isToolValid()) {
                    val name = Tool.current.name
                    smallImage(name.lowercase(), name)
                }

                // TODO: add party support for servers

                timestamps(System.currentTimeMillis())
            }
        }
    }
}