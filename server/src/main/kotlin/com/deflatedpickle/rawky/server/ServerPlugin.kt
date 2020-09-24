package com.deflatedpickle.rawky.server

import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.event.EventWindowShown
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.rawky.server.backend.request.Request
import com.deflatedpickle.rawky.server.backend.request.RequestMoveMouse
import com.deflatedpickle.rawky.server.backend.response.ResponseMoveMouse
import com.deflatedpickle.rawky.server.frontend.menu.MenuServer
import com.deflatedpickle.rawky.server.frontend.widget.ServerPanel
import com.deflatedpickle.rawky.ui.window.Window
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import org.apache.logging.log4j.LogManager
import java.awt.Point
import java.io.IOException
import java.net.InetSocketAddress
import java.util.*
import javax.swing.JMenu

@Plugin(
    value = "server",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A basic server
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#1.0.0"
    ]
)
@Suppress("unused")
object ServerPlugin {
    var client = Client()
    var server: Server = Server()

    val uuid: UUID = UUID.randomUUID()

    @Suppress("HasPlatformType")
    val logger = LogManager.getLogger()

    init {
        EventProgramFinishSetup.addListener {
            val menuBar = RegistryUtil.get(MenuCategory.MENU.name)
            val toolMenu = menuBar?.get(MenuCategory.TOOLS.name) as JMenu

            toolMenu.add(MenuServer)
        }

        EventWindowShown.addListener {
            if (it is Window) {
                ServerPanel.rootPane = Window.rootPane
                Window.glassPane = ServerPanel
                Window.glassPane.isVisible = true
            }
        }

        client.addListener(object : Listener() {
            override fun connected(connection: Connection) {
                logger.info("Connected to ${connection.remoteAddressTCP}")
            }

            override fun received(connection: Connection, any: Any) {
                logger.trace("Received $any from ${connection.remoteAddressTCP}")

                when (any) {
                    is ResponseMoveMouse -> {
                        if (any.point != null) {
                            ServerPanel.cursorPositions[any.id] = any.point
                            ServerPanel.repaint()
                        }
                    }
                }
            }
        })

        server.addListener(object : Listener() {
            override fun connected(connection: Connection) {
                logger.info("Connected to ${connection.remoteAddressTCP}")
            }

            override fun received(connection: Connection, any: Any) {
                logger.trace("Received $any from ${connection.remoteAddressTCP}")

                if (any is Request) {
                    when (any) {
                        is RequestMoveMouse -> {
                            server.sendToAllTCP(
                                ResponseMoveMouse(
                                    connection.id,
                                    any.point
                                )
                            )
                        }
                    }
                }
            }
        })

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                client.close()
                server.close()
            }
        })

        for (kryo in listOf(
            this.client.kryo,
            this.server.kryo
        )) {
            this.registerRequests(kryo)
            this.registerResponses(kryo)
            this.registerSerializers(kryo)
        }
    }

    private fun registerRequests(kryo: Kryo) {
        with(kryo) {
            register(RequestMoveMouse::class.java)
        }
    }

    private fun registerResponses(kryo: Kryo) {
        with(kryo) {
            register(ResponseMoveMouse::class.java)
        }
    }

    private fun registerSerializers(kryo: Kryo) {
        with(kryo) {
            register(Point::class.java)
        }
    }

    /**
     * Starts and connects to a server
     */
    @Throws(IOException::class)
    fun startServer(tcpPort: Int, udpPort: Int) {
        this.server.start()
        this.server.bind(tcpPort, udpPort)
    }

    /**
     * Connects to a server
     */
    @Throws(IOException::class)
    fun connectServer(timeoutMilliseconds: Int, ipAddress: String, tcpPort: Int, udpPort: Int) {
        this.client.start()
        this.client.connect(
            timeoutMilliseconds,
            ipAddress,
            tcpPort,
            udpPort
        )
    }
}