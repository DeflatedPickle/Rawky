package com.deflatedpickle.rawky.server.leaderboard.request

import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.request.Request
import com.deflatedpickle.rawky.server.leaderboard.LeaderboardPanel
import com.deflatedpickle.rawky.server.leaderboard.LeaderboardPlugin
import com.deflatedpickle.rawky.server.leaderboard.query.QueryUserStats
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server

data class RequestSyncOldStats(
    val id: Int = -1,
    val limit: Int = -1,
) : Request() {
    override fun runServer(connection: Connection, server: Server) {
        ServerPlugin.logger.info("Sending previous stats to $id")

        for ((u, s) in LeaderboardPlugin.users) {
            server.sendToTCP(id, QueryUserStats(u.id, s))
        }
    }
}