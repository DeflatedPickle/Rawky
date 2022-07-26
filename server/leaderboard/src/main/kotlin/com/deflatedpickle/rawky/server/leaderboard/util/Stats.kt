package com.deflatedpickle.rawky.server.leaderboard.util

import java.awt.Color
import java.time.Instant

data class Stats(
    val pixelsPlaced: Int = 0,
    val coloursUsed: MutableList<Color> = mutableListOf(),
    var joinTime: Instant = Instant.now(),
)