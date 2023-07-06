/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.server.leaderboard

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.api.util.ComponentPosition
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.event.EventJoinServer
import com.deflatedpickle.rawky.server.backend.event.EventRegisterPackets
import com.deflatedpickle.rawky.server.backend.event.EventUserJoinServer
import com.deflatedpickle.rawky.server.backend.util.User
import com.deflatedpickle.rawky.server.leaderboard.query.QueryUserStats
import com.deflatedpickle.rawky.server.leaderboard.request.RequestSyncOldStats
import com.deflatedpickle.rawky.server.leaderboard.util.Stats
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively
import java.time.Instant
import java.util.Vector

@Plugin(
    value = "leaderboard",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A widget to keep track of stats
    """,
    type = PluginType.COMPONENT,
    component = LeaderboardPanel::class,
    dependencies = [
        "deflatedpickle@core#*",
        "deflatedpickle@server#*",
    ],
)
object LeaderboardPlugin {
    // Used on server only
    val users = mutableMapOf<User, Stats>()
    val stats = Vector<Vector<Any>>()

    init {
        EventRegisterPackets.addListener {
            with(it.first) {
                register(Instant::class.java)

                register(Stats::class.java)

                register(QueryUserStats::class.java)
                register(RequestSyncOldStats::class.java)
            }
        }

        EventUserJoinServer.addListener { u ->
            ServerPlugin.server?.let {
                users[u] = Stats()
            }

            ServerPlugin.client.sendTCP(QueryUserStats(u.id))
        }

        EventJoinServer.addListener {
            ServerPlugin.client.sendTCP(RequestSyncOldStats(ServerPlugin.id))
        }

        EventChangeTheme.addListener {
            LeaderboardPanel.updateUIRecursively()
        }
    }
}
