/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.icupnp.UPnP
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.event.EventChangeTool
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.rawky.server.backend.api.Destination.CLIENT
import com.deflatedpickle.rawky.server.backend.api.Destination.SERVER
import com.deflatedpickle.rawky.server.backend.api.packet.ClientPacket
import com.deflatedpickle.rawky.server.backend.api.packet.ServerPacket
import com.deflatedpickle.rawky.server.backend.event.EventDisconnect
import com.deflatedpickle.rawky.server.backend.event.EventJoinServer
import com.deflatedpickle.rawky.server.backend.event.EventRegisterPackets
import com.deflatedpickle.rawky.server.backend.event.EventStartServer
import com.deflatedpickle.rawky.server.backend.query.QueryChangeColour
import com.deflatedpickle.rawky.server.backend.query.QueryChangeTool
import com.deflatedpickle.rawky.server.backend.query.QueryUpdateCell
import com.deflatedpickle.rawky.server.backend.request.RequestChangeName
import com.deflatedpickle.rawky.server.backend.request.RequestMoveMouse
import com.deflatedpickle.rawky.server.backend.request.RequestRemoveUser
import com.deflatedpickle.rawky.server.backend.request.RequestUserJoin
import com.deflatedpickle.rawky.server.backend.request.RequestUserLeave
import com.deflatedpickle.rawky.server.backend.response.ResponseActiveUsers
import com.deflatedpickle.rawky.server.backend.response.ResponseChangeName
import com.deflatedpickle.rawky.server.backend.response.ResponseJoinFail
import com.deflatedpickle.rawky.server.backend.response.ResponseMoveMouse
import com.deflatedpickle.rawky.server.backend.response.ResponseNewDocument
import com.deflatedpickle.rawky.server.backend.response.ResponseUserJoin
import com.deflatedpickle.rawky.server.backend.response.ResponseUserLeave
import com.deflatedpickle.rawky.server.backend.serializer.ColorSerializer
import com.deflatedpickle.rawky.server.backend.util.JoinFail
import com.deflatedpickle.rawky.server.backend.util.LeaveReason
import com.deflatedpickle.rawky.server.backend.util.Role
import com.deflatedpickle.rawky.server.backend.util.ServerProperties
import com.deflatedpickle.rawky.server.backend.util.User
import com.deflatedpickle.rawky.server.backend.util.UserUpdate
import com.deflatedpickle.rawky.server.frontend.menu.MenuServer
import com.deflatedpickle.rawky.server.frontend.widget.ServerPanel
import com.deflatedpickle.undulation.builder.ProgressMonitorBuilder
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.Color
import java.awt.Component
import java.awt.Point
import java.awt.Rectangle
import java.io.IOException
import javax.swing.ImageIcon
import javax.swing.JMenu
import kotlin.collections.LinkedHashMap

@OptIn(DelicateCoroutinesApi::class)
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
        "deflatedpickle@core#*"
    ],
    settings = ServerSettings::class
)
@Suppress("unused")
object ServerPlugin {
    val logger: Logger = LogManager.getLogger()

    const val portMin = 49_152
    const val portMax = 65_535

    var client = Client()
    var server: Server? = null

    // Maybe replace this with the User instance?
    var id = -1
        private set

    lateinit var serverProperties: ServerProperties

    var userMap = mutableMapOf<Int, User>()
    var grid: Grid? = null

    lateinit var oldPane: Component

    init {
        EventProgramFinishSetup.addListener {
            val menuBar = RegistryUtil.get(MenuCategory.MENU.name)
            val toolMenu = menuBar?.get(MenuCategory.TOOLS.name) as JMenu

            toolMenu.add(MenuServer)

            GlobalScope.launch {
                UPnP.isUPnPAvailable()
            }
        }

        EventJoinServer.addListener {
            oldPane = Haruhi.window.glassPane

            ServerPanel.rootPane = Haruhi.window.rootPane
            Haruhi.window.glassPane = ServerPanel
            Haruhi.window.glassPane.isVisible = true
        }

        EventDisconnect.addListener {
            Haruhi.window.glassPane = oldPane
        }

        EventChangeTool.addListener {
            client.sendTCP(QueryChangeTool(id, Tool.current, it))
        }

        EventChangeColour.addListener {
            // client.sendTCP(QueryChangeColour(id, RawkyPlugin.colour))
        }

        EventRegisterPackets.addListener {
            with(it.first) {
                registerQueries(this)
                registerRequests(this)
                registerResponses(this)
                registerSerializers(this)
            }
        }

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                if (client.updateThread != null) {
                    logger.info("Closing the client thread")
                    client.stop()
                }

                server?.let { server ->
                    if (server.updateThread != null) {
                        logger.info("Closing the server thread")
                        server.close()
                    }
                }

                if (this@ServerPlugin::serverProperties.isInitialized) {
                    logger.info("Closing the TCP port: ${serverProperties.tcpPort}")
                    UPnP.closePortTCP(serverProperties.tcpPort)
                    logger.info("Closing the UDP port: ${serverProperties.udpPort}")
                    UPnP.closePortUDP(serverProperties.udpPort)
                }
            }
        })
    }

    private fun registerQueries(kryo: Kryo) {
        with(kryo) {
            register(QueryUpdateCell::class.java)
            register(QueryChangeTool::class.java)
            register(QueryChangeColour::class.java)
        }
    }

    private fun registerRequests(kryo: Kryo) {
        with(kryo) {
            register(RequestMoveMouse::class.java)

            register(RequestUserJoin::class.java)
            register(RequestUserLeave::class.java)
            register(RequestRemoveUser::class.java)

            register(RequestChangeName::class.java)
        }
    }

    private fun registerResponses(kryo: Kryo) {
        with(kryo) {
            register(ResponseMoveMouse::class.java)

            register(ResponseUserJoin::class.java)
            register(ResponseUserLeave::class.java)

            register(ResponseJoinFail::class.java)
            register(ResponseActiveUsers::class.java)
            register(ResponseChangeName::class.java)

            register(ResponseNewDocument::class.java)
        }
    }

    private fun registerSerializers(kryo: Kryo) {
        with(kryo) {
            register(Function::class.java)

            register(LinkedHashMap::class.java)
            register(FloatArray::class.java)

            register(Point::class.java)
            register(Rectangle::class.java)
            register(Color::class.java, ColorSerializer(kryo))
            register(ImageIcon::class.java)

            register(JoinFail::class.java)
            register(LeaveReason::class.java)
            register(UserUpdate::class.java)

            register(User::class.java)
            register(Role::class.java)

            register(Cell::class.java)

            for (v in Tool.registry.values) {
                register(v::class.java)
            }
        }
    }

    /**
     * Starts and connects to a server
     */
    @Throws(IOException::class)
    fun startServer(tcpPort: Int, udpPort: Int, progressMonitor: ProgressMonitorBuilder) {
        progressMonitor
            .queue {
                note = "Setting server instance"
                task = { Server().let { server = it } }
            }
            .queue {
                note = "Adding server listener"
                task = { addServerListener() }
            }
            .queue {
                note = "Registering packets"
                task = { EventRegisterPackets.trigger(Pair(server!!.kryo, SERVER)) }
            }
            .queue {
                note = "Starting server"
                task = {
                    server!!.let { server ->
                        if (server.updateThread == null) {
                            server.start()
                        }
                    }
                }
            }
            .queue {
                note = "Binding server to ports"
                task = {
                    server!!.bind(
                        tcpPort,
                        udpPort
                    )
                }
            }
            .queue {
                note = "Setting server properties"
                task = {
                    ServerProperties(
                        tcpPort,
                        udpPort
                    ).let { serverProperties = it }
                }
            }
            .queue {
                note = "Triggering the start server event"
                task = { EventStartServer.trigger(null) }
            }
    }

    fun closeServer() {
        leaveServer()
        userMap.clear()

        server?.let { server ->
            server.sendToAllTCP(
                ResponseActiveUsers(
                    userMap
                )
            )
            server.close()
        }

        server = null
    }

    private fun addServerListener() {
        server?.let { server ->
            server.addListener(object : Listener() {
                override fun connected(connection: Connection) {
                    logger.info("Connected to ${connection.remoteAddressTCP}")
                }

                override fun received(connection: Connection, any: Any) {
                    logger.trace("Received $any from ${connection.remoteAddressTCP}")

                    if (any is ServerPacket) {
                        any.runServer(connection, server)
                    }
                }
            })
        }
    }

    /**
     * Connects to a server
     */
    @Throws(IOException::class)
    fun connectServer(
        timeoutMilliseconds: Int,
        ipAddress: String,
        tcpPort: Int,
        udpPort: Int,
        userName: String,
        progressMonitor: ProgressMonitorBuilder,
        retries: Int?,
    ) {
        progressMonitor
            .queue {
                note = "Adding client listener"
                task = { addClientListener() }
            }
            .queue {
                note = "Registering packets"
                task = { EventRegisterPackets.trigger(Pair(client.kryo, CLIENT)) }
            }
            .queue {
                note = "Starting the client"
                task = {
                    if (client.updateThread == null) {
                        client.start()
                    }
                }
            }
            .queue {
                note = "Connecting to the server"
                task = {
                    // This sometimes fails so we may as well retry it
                    for (i in 0 until (retries ?: 1)) {
                        try {
                            client.connect(
                                timeoutMilliseconds,
                                ipAddress,
                                tcpPort,
                                udpPort
                            )
                        } catch (e: Exception) {
                            continue
                        }

                        if (client.isConnected) {
                            break
                        }
                    }
                }
            }
            .queue {
                note = "Sending a join request"
                task = { client.sendUDP(RequestUserJoin(userName)) }
            }
            .queue {
                note = "Disabling toolbar"
                task = {
                    if (server == null) {
                        for (i in Haruhi.toolbar.components) {
                            i.isEnabled = false
                        }
                    }
                }
            }
            .queue {
                note = "Triggering the join server event"
                task = { EventJoinServer.trigger() }
            }
    }

    fun leaveServer() {
        EventDisconnect.trigger(userMap[id]!!)
        client.sendTCP(RequestUserLeave(id))
        userMap.clear()
        client.stop()
        client = Client()

        for (i in Haruhi.toolbar.components) {
            i.isEnabled = true
        }
    }

    private fun addClientListener() {
        client.addListener(object : Listener() {
            override fun connected(connection: Connection) {
                logger.info("Connected to ${connection.remoteAddressTCP}")
                id = connection.id
            }

            override fun received(connection: Connection, any: Any) {
                logger.trace("Received $any from ${connection.remoteAddressTCP}")

                if (any is ClientPacket) {
                    any.runClient(connection, client)
                }

                ServerPanel.repaint()
            }
        })
    }

    fun upnpStart(tcpPort: Int, udpPort: Int) {
        if (UPnP.isUPnPAvailable()) {
            logger.info("Attempting UPnP port forwarding")

            if (UPnP.isMappedTCP(tcpPort)) {
                logger.warn("TCP port '$tcpPort' already mapped")
                if (UPnP.closePortTCP(tcpPort)) {
                    logger.warn("Closed TCP port '$tcpPort'")
                }
            }
            if (UPnP.openPortTCP(tcpPort)) {
                logger.warn("Opened TCP port '$tcpPort'")
            } else {
                logger.warn("Failed to open TCP port '$tcpPort'")
            }

            if (UPnP.isMappedUDP(udpPort)) {
                logger.warn("UDP port '$udpPort' already mapped")
                if (UPnP.closePortUDP(udpPort)) {
                    logger.warn("Closed UDP port '$udpPort'")
                }
            }
            if (UPnP.openPortUDP(udpPort)) {
                logger.warn("Opened UDP port '$udpPort'")
            } else {
                logger.warn("Failed to open UDP port '$udpPort'")
            }
        } else {
            logger.warn("UPnP port-forwarding is not available on your network")
        }
    }

    fun updateCell(any: QueryUpdateCell) {
        RawkyPlugin.document?.let { doc ->
            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]
            val grid = layer.child

            any.cell?.let { cell ->
                grid[cell.row, cell.column + 1] {
                    // TODO
                    // colour = cell.colour
                }
            }

            EventUpdateGrid.trigger(grid)
        }
    }
}
