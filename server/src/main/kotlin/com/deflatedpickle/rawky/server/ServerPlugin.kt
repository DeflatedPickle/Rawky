package com.deflatedpickle.rawky.server

import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.marvin.Colour
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.rawky.server.backend.event.EventJoinServer
import com.deflatedpickle.rawky.server.backend.event.EventStartServer
import com.deflatedpickle.rawky.server.backend.request.RequestMoveMouse
import com.deflatedpickle.rawky.server.backend.request.RequestUserJoin
import com.deflatedpickle.rawky.server.backend.response.ResponseActiveUsers
import com.deflatedpickle.rawky.server.backend.response.ResponseJoinFail
import com.deflatedpickle.rawky.server.backend.response.ResponseMoveMouse
import com.deflatedpickle.rawky.server.backend.response.ResponseNewDocument
import com.deflatedpickle.rawky.server.backend.query.QueryUpdateCell
import com.deflatedpickle.rawky.server.backend.request.RequestUserLeave
import com.deflatedpickle.rawky.server.backend.response.ResponseUserJoin
import com.deflatedpickle.rawky.server.backend.response.ResponseUserLeave
import com.deflatedpickle.rawky.server.backend.util.JoinFail
import com.deflatedpickle.rawky.server.backend.util.ServerProperties
import com.deflatedpickle.rawky.server.backend.util.User
import com.deflatedpickle.rawky.server.frontend.dialog.DialogConnectServer
import com.deflatedpickle.rawky.server.frontend.menu.MenuServer
import com.deflatedpickle.rawky.server.frontend.widget.ServerPanel
import com.deflatedpickle.rawky.util.ActionUtil
import com.deflatedpickle.undulation.builder.ProgressMonitorBuilder
import com.dosse.upnp.UPnP
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import java.awt.Point
import java.awt.Rectangle
import java.io.IOException
import java.util.*
import javax.swing.JMenu
import javax.swing.ProgressMonitor
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
    private val logger = LogManager.getLogger()

    var client = Client()
    var server: Server? = null

    // Maybe replace this with the User instance?
    var id = -1
        private set

    lateinit var serverProperties: ServerProperties

    var userMap = mutableMapOf<Int, User>()
    var grid: Grid? = null

    init {
        EventProgramFinishSetup.addListener {
            val menuBar = RegistryUtil.get(MenuCategory.MENU.name)
            val toolMenu = menuBar?.get(MenuCategory.TOOLS.name) as JMenu

            toolMenu.add(MenuServer)

            ServerPanel.rootPane = PluginUtil.window.rootPane
            PluginUtil.window.glassPane = ServerPanel
            PluginUtil.window.glassPane.isVisible = true

            GlobalScope.launch {
                UPnP.isUPnPAvailable()
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
        }
    }

    private fun registerRequests(kryo: Kryo) {
        with(kryo) {
            register(RequestMoveMouse::class.java)

            register(RequestUserJoin::class.java)
            register(RequestUserLeave::class.java)
        }
    }

    private fun registerResponses(kryo: Kryo) {
        with(kryo) {
            register(ResponseMoveMouse::class.java)

            register(ResponseUserJoin::class.java)
            register(ResponseJoinFail::class.java)

            register(ResponseJoinFail::class.java)
            register(ResponseActiveUsers::class.java)

            register(ResponseNewDocument::class.java)
        }
    }

    private fun registerSerializers(kryo: Kryo) {
        with(kryo) {
            register(LinkedHashMap::class.java)
            register(FloatArray::class.java)

            register(Point::class.java)
            // register(Color::class.java)
            register(Rectangle::class.java)

            register(Colour::class.java)

            register(JoinFail::class.java)
            register(User::class.java)

            register(Cell::class.java)
            // register(Array<Cell>::class.java)
            // register(Grid::class.java)
            // register(Layer::class.java)
            // register(Array<Layer>::class.java)
            // register(Frame::class.java)
            // register(Array<Frame>::class.java)
            // register(RawkyDocument::class.java)
        }
    }

    /**
     * Starts and connects to a server
     */
    @Throws(IOException::class)
    fun startServer(tcpPort: Int, udpPort: Int) {
        if (server == null) {
            this.server = Server()
            this.addServerListener()

            server?.let { server ->
                registerQueries(server.kryo)
                this.registerRequests(server.kryo)
                this.registerResponses(server.kryo)
                this.registerSerializers(server.kryo)
            }
        }

        server?.let { server ->
            if (server.updateThread == null) {
                server.start()
            }

            server.bind(
                tcpPort,
                udpPort
            )
        }

        this.serverProperties = ServerProperties(
            tcpPort,
            udpPort
        )

        EventStartServer.trigger(null)
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

                    when (any) {
                        is RequestMoveMouse -> {
                            server.sendToAllTCP(
                                ResponseMoveMouse(
                                    connection.id,
                                    any.point
                                )
                            )
                        }
                        is RequestUserJoin -> {
                            logger.info("${any.userName} joined")

                            userMap[connection.id] = User(
                                id = connection.id,
                                userName = any.userName
                            )

                            server.sendToAllTCP(
                                ResponseActiveUsers(
                                    userMap
                                )
                            )
                        }
                        is RequestUserLeave -> {
                            logger.info("${any.userName} left")

                            userMap.remove(connection.id)
                            ServerPanel.repaint()

                            server.sendToAllTCP(
                                ResponseActiveUsers(
                                    userMap
                                )
                            )
                        }
                        is QueryUpdateCell -> updateCell(any)
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
        tcpPort: Int, udpPort: Int,
        userName: String,
        progressMonitor: ProgressMonitorBuilder,
        retries: Int?,
    ) {
        progressMonitor
            .queue {
                note = "Adding client listener..."
                task = { addClientListener() }
            }
            .queue {
                note = "Registering queries..."
                task = { registerQueries(client.kryo) }
            }
            .queue {
                note = "Registering requests..."
                task = { registerRequests(client.kryo) }
            }
            .queue {
                note = "Registering responses..."
                task = { registerResponses(client.kryo) }
            }
            .queue {
                note = "Registering serializers..."
                task = { registerSerializers(client.kryo) }
            }
            .queue {
                note = "Starting the client..."
                task = {
                    if (client.updateThread == null) {
                        client.start()
                    }
                }
            }
            .queue {
                note = "Connecting to the server..."
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
                note = "Sending a join request..."
                task = { client.sendUDP(RequestUserJoin(userName)) }
            }
            .queue {
                note = "Triggering the join server event..."
                task = { EventJoinServer.trigger(null) }
            }
    }

    fun leaveServer() {
        client.sendTCP(RequestUserLeave())
        userMap.clear()
        client.stop()
        client = Client()
    }

    private fun addClientListener() {
        client.addListener(object : Listener() {
            override fun connected(connection: Connection) {
                logger.info("Connected to ${connection.remoteAddressTCP}")
                id = connection.id
            }

            override fun received(connection: Connection, any: Any) {
                logger.trace("Received $any from ${connection.remoteAddressTCP}")

                when (any) {
                    is ResponseMoveMouse -> {
                        if (any.point != null) {
                            userMap[any.id]?.mousePosition?.location = any.point
                            ServerPanel.repaint()
                        }
                    }
                    is ResponseUserJoin -> logger.info("${any.userName} joined")
                    is ResponseUserLeave -> logger.info("${any.userName} left")
                    is ResponseJoinFail -> logger.warn("Failed to join due to ${any.reason}")
                    is ResponseActiveUsers -> userMap.putAll(any.activeUsers)
                    is ResponseNewDocument -> {
                        if (server == null) {
                            logger.debug("Creating a new document from server info")
                            RawkyPlugin.document = ActionUtil.newDocument(
                                any.rows,
                                any.columns,
                                any.frames,
                                any.layers,
                            )
                            EventCreateDocument.trigger(RawkyPlugin.document!!)
                        }
                    }
                    is QueryUpdateCell -> updateCell(any)
                }
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
                grid[cell.row, cell.column] {
                    colour = cell.colour
                }
            }

            EventUpdateGrid.trigger(grid)
        }
    }
}