@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.rawky.server.leaderboard.query

import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.query.Query
import com.deflatedpickle.rawky.server.leaderboard.LeaderboardPanel
import com.deflatedpickle.rawky.server.leaderboard.LeaderboardPlugin
import com.deflatedpickle.rawky.server.leaderboard.util.Stats
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server
import java.util.*
import javax.swing.event.TableModelEvent

class QueryUserStats(
    val id: Int = -1,
    val stats: Stats? = null,
) : Query() {
    override fun runServer(connection: Connection, server: Server) {
        server.sendToAllTCP(
            if (stats == null) {
                QueryUserStats(id, LeaderboardPlugin.users[ServerPlugin.userMap[id]])
            } else {
                this
            }
        )
    }

    override fun runClient(connection: Connection, client: Client) {
        try {
            LeaderboardPanel.model.setValueAt(null, id - 1, 0)
        } catch (e: IndexOutOfBoundsException) {
            for (i in 0 until id) {
                LeaderboardPanel.model.addRow(arrayOf())
            }
        }

        LeaderboardPanel.model.setValueAt(ServerPlugin.userMap[id]!!, id - 1, 0)
        LeaderboardPanel.model.setValueAt(stats?.pixelsPlaced, id - 1, 1)
        LeaderboardPanel.model.setValueAt(stats?.coloursUsed, id - 1, 2)
        LeaderboardPanel.model.setValueAt(stats?.joinTime, id - 1, 3)

        LeaderboardPanel.model.newDataAvailable(
            TableModelEvent(
                LeaderboardPanel.model,
                id - 1,
            )
        )
    }
}